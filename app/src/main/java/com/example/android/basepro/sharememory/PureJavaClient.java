package com.example.android.basepro.sharememory;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.ParcelFileDescriptor;
import android.os.SharedMemory;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * 工业级稳定性 IPC 客户端
 * * 核心机制：
 * 1. Seqlock (顺序锁): 解决无锁读写并发导致的“数据撕裂”问题。
 * 2. Packet Magic: 解决 RingBuffer 覆盖写导致的“脏数据解析”问题。
 * 3. Padding Protocol: 解决变长数据在环形缓冲区的回绕问题。
 * 4. Resync Strategy: 当检测到冲突或覆盖时，自动跳跃至最新数据。
 */
public class PureJavaClient {
    private static final String TAG = "PureJavaClient";

    // --- 内存布局定义 (必须与 C++ 严格对齐) ---
    // C++ struct SharedHeader {
    //    atomic<u32> write_pos;  // Offset 0
    //    u32 padding;            // Offset 4 (64位系统为了对齐 u64 自动插入)
    //    atomic<u64> seqlock;    // Offset 8
    //    u32 data_size;          // Offset 16
    //    u32 header_magic;       // Offset 20
    // }; -> Total 24 Bytes

    private static final int OFFSET_WRITE_POS = 0;
    private static final int OFFSET_SEQLOCK = 8;
    private static final int OFFSET_DATA_SIZE = 16;
    private static final int OFFSET_GLOBAL_MAGIC = 20;
    private static final int HEADER_TOTAL_SIZE = 24;

    // 数据包魔数，用于校验 Packet 有效性
    private static final int PACKET_MAGIC = 0xAABBCCDD;

    private SharedMemory mSharedMemory;
    private ByteBuffer mMapping;
    private FileInputStream mEventStream;
    private ParcelFileDescriptor mPfdEvent;

    private int mCapacity;
    private int mLocalReadPos = 0; // 客户端本地维护的读取指针
    private DataListener mListener;

    public interface DataListener {
        void onMessage(String msg);
    }

    public void setListener(DataListener listener) {
        this.mListener = listener;
    }

    public void connect(SharedMemory sharedMemory, ParcelFileDescriptor pfdEvent) {
        mSharedMemory = sharedMemory;
        mPfdEvent = pfdEvent;

        try {
            if (mSharedMemory == null || mPfdEvent == null) return;

            // 1. 映射共享内存 (Read Only 模式，保护内存不被篡改)
            mMapping = mSharedMemory.mapReadOnly();
            mMapping.order(ByteOrder.LITTLE_ENDIAN);

            // 简单检查全局 Magic
            int globalMagic = mMapping.getInt(OFFSET_GLOBAL_MAGIC);
            // 校验失败也不阻断，因为 C++ 初始化可能有延迟，主要依赖 Seqlock

            mCapacity = mMapping.getInt(OFFSET_DATA_SIZE);
            if (mCapacity == 0) mCapacity = mSharedMemory.getSize() - HEADER_TOTAL_SIZE;

            Log.i(TAG, "IPC Connected. Capacity: " + mCapacity + " GlobalMagic: " + Integer.toHexString(globalMagic));

            // 2. 准备 EventFD 流
            mEventStream = new FileInputStream(mPfdEvent.getFileDescriptor());

            // 3. 注册 Epoll 监听
            Looper.myQueue().addOnFileDescriptorEventListener(
                    mPfdEvent.getFileDescriptor(),
                    MessageQueue.OnFileDescriptorEventListener.EVENT_INPUT,
                    new MessageQueue.OnFileDescriptorEventListener() {
                        @Override
                        public int onFileDescriptorEvents(FileDescriptor fd, int events) {
                            // 当收到 Service 的信号时，回调这里
                            handleEvent();
                            // 返回 EVENT_INPUT 表示继续监听，返回 0 表示移除监听
                            return MessageQueue.OnFileDescriptorEventListener.EVENT_INPUT;
                        }
                    }
            );

        } catch (ErrnoException e) {
            e.printStackTrace();
            Log.e(TAG, "Connection failed: " + e.getMessage());
        }
    }

    private void handleEvent() {
        try {
            // A. 必须消费 EventFD 计数 (8 bytes)
            byte[] buf = new byte[8];
            if (mEventStream.read(buf) != 8) return;

            // B. 进入读取循环
            readLoop();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 核心读取逻辑：包含 Seqlock 校验和 Magic 校验
     */
    private void readLoop() {
        int loopSafety = 0;

        while (true) {
            // 熔断保护，防止逻辑错误死循环
            if (loopSafety++ > 2000) {
                Log.w(TAG, "Loop limit reached, forcing sync.");
                resetToLatest();
                break;
            }

            // --- Seqlock Step 1: 读取开始版本号 ---
            long seqBegin = mMapping.getLong(OFFSET_SEQLOCK);

            // 检查 1: 如果版本号是奇数，说明 Writer 正在修改内存
            // 策略: 此时读到的数据肯定是脏的，直接放弃本次读取，等待下一次通知或重置
            if ((seqBegin & 1) != 0) {
                // Writer 正在忙，我们直接跳到最新位置，放弃旧数据
                resetToLatest();
                return;
            }

            // 获取服务端当前的写位置
            int serverWritePos = mMapping.getInt(OFFSET_WRITE_POS);

            // 如果追上了，结束循环
            if (mLocalReadPos == serverWritePos) return;

            // --- 开始读取 Packet ---

            // 1. 物理边界预检查
            // 如果连 Packet Header (Magic 4 + Len 4 = 8 bytes) 都读不了，说明必定回绕了
            if (mLocalReadPos + 8 > mCapacity) {
                mLocalReadPos = 0; // 回绕到头部
                continue;
            }

            // 2. 校验 Packet Magic
            // 这是检测“数据是否被覆盖”的第一道防线
            int magic = mMapping.getInt(HEADER_TOTAL_SIZE + mLocalReadPos);

            if (magic != PACKET_MAGIC) {
                // Magic 不对，只有两种可能：
                // A. 这是 Padding 区域（C++ 代码在 Padding 时保留了旧的 Magic，但 Len=0）
                // B. 数据被 Writer 覆盖了，Magic 被改写成了 Payload 的一部分

                // 我们先读 Length 看看是不是 Padding
                int potentialLen = mMapping.getInt(HEADER_TOTAL_SIZE + mLocalReadPos + 4);
                if (potentialLen == 0) {
                    // 是 Padding，正常回绕
                    mLocalReadPos = 0;
                    continue;
                }

                // 既不是 Padding，Magic 又不对 -> 必定是被覆盖了 (Data Corruption)
                Log.w(TAG, "Magic mismatch (0x" + Integer.toHexString(magic) + "). Data overwritten?");
                resetToLatest();
                return;
            }

            // 3. 读取 Length
            int len = mMapping.getInt(HEADER_TOTAL_SIZE + mLocalReadPos + 4);

            // 检查 Padding (C++ 在剩余空间不足时会写入 0)
            if (len == 0) {
                mLocalReadPos = 0;
                continue;
            }

            // 4. 数据有效性检查
            // 如果数据长度超出了剩余空间，或者是个负数 -> 数据损坏
            if (len < 0 || mLocalReadPos + 8 + len > mCapacity) {
                Log.e(TAG, "Invalid length detected: " + len);
                resetToLatest();
                return;
            }

            // 5. 读取数据 Body
            // 使用 duplicate 避免影响 mMapping 全局 position
            ByteBuffer readBuf = mMapping.duplicate();
            readBuf.order(ByteOrder.LITTLE_ENDIAN);

            byte[] data = new byte[len];
            try {
                readBuf.position(HEADER_TOTAL_SIZE + mLocalReadPos + 8);
                readBuf.get(data);
            } catch (Exception e) {
                // 极端情况下的 BufferUnderflow
                Log.e(TAG, "Read failed: " + e.getMessage());
                resetToLatest();
                return;
            }

            // --- Seqlock Step 2: 读取结束版本号 ---
            long seqEnd = mMapping.getLong(OFFSET_SEQLOCK);

            // --- Seqlock Step 3: 最终裁决 ---
            // 如果 seqBegin != seqEnd，说明在我们读取这几行代码的过程中，Writer 插入了新数据。
            // 这意味着 data 数组里的数据可能是“前一半是旧包，后一半是新包”，也就是撕裂了。
            if (seqBegin != seqEnd) {
                Log.w(TAG, "Seqlock conflict (Tearing detected). Drop packet.");
                // 数据不可信，直接丢弃，重置到最新
                resetToLatest();
                return;
            }

            // 6. 校验通过，回调数据
            if (mListener != null) {
                mListener.onMessage(new String(data));
            }

            // 移动读取指针
            mLocalReadPos += (8 + len);
        }
    }

    /**
     * 重置指针到最新的写位置 (Resync)
     * 当发生数据冲突或覆盖时调用
     */
    private void resetToLatest() {
        // 直接读取原子变量 write_pos
        int latest = mMapping.getInt(OFFSET_WRITE_POS);
        Log.i(TAG, "Resyncing consumer to: " + latest);
        mLocalReadPos = latest;
    }

    public void disconnect() {
        if (mSharedMemory != null) {
            mSharedMemory.close();
        }
        try {
            if (mEventStream != null) mEventStream.close();
            if (mPfdEvent != null) mPfdEvent.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
