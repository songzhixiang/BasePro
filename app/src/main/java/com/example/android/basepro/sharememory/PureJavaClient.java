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

public class PureJavaClient {
    private static final String TAG = "PureJavaClient";

    // 对应 C++ struct SharedHeader 的布局
    // struct SharedHeader {
    //     atomic<uint32_t> write_pos; // offset 0
    //     atomic<uint32_t> read_pos;  // offset 4 (客户端忽略这个，自己维护进度)
    //     uint32_t data_size;         // offset 8
    //     uint32_t magic;             // offset 12
    // };
    // 内存布局偏移量
    private static final int HEADER_SIZE = 16;
    private static final int OFFSET_WRITE_POS = 0;
    private static final int OFFSET_DATA_SIZE = 8;
    private static final int OFFSET_MAGIC = 12;

    private SharedMemory mSharedMemory;
    private ByteBuffer mMapping;
    private ParcelFileDescriptor mPfdEvent;
    private FileInputStream mEventStream; // 用于读取 eventfd 的 8字节计数

    private int mCapacity; // 有效数据区大小
    private int mLocalReadPos = 0; // 客户端本地读取指针
    private DataListener mListener;

    public interface DataListener {
        void onMessage(String msg);
    }

    public void setListener(DataListener listener) {
        this.mListener = listener;
    }

    /**
     * 连接 IPC 通道

     * @param pfdEvent 通知 FD (eventfd)
     */
    public void connect(SharedMemory sharedMemory, ParcelFileDescriptor pfdEvent)  {
        mSharedMemory = sharedMemory;
        mPfdEvent = pfdEvent;
        try {
            if (mSharedMemory == null || mPfdEvent == null) return;
            // 1. 映射共享内存 (Read Only)
            mMapping = mSharedMemory.mapReadOnly();
            // C++ 默认通常是 Little Endian
            mMapping.order(ByteOrder.LITTLE_ENDIAN);

            // 校验 Magic
            int magic = mMapping.getInt(OFFSET_MAGIC);
            if (magic != 0x12345678) {
                Log.e(TAG, "Magic number mismatch!");
                return;
            }

            mCapacity = mMapping.getInt(OFFSET_DATA_SIZE);
            Log.i(TAG, "IPC Connected. Capacity: " + mCapacity + ", Magic: " + Integer.toHexString(magic));

            // 2. 准备 EventFD 的读取流
            // eventfd 必须读取 8 个字节才能清除信号，否则 epoll 会一直触发
            mEventStream = new FileInputStream(pfdEvent.getFileDescriptor());

            // 3. 注册到当前线程的 Looper (例如主线程)
            Looper.myQueue().addOnFileDescriptorEventListener(
                    pfdEvent.getFileDescriptor(),
                    MessageQueue.OnFileDescriptorEventListener.EVENT_INPUT,
                    new MessageQueue.OnFileDescriptorEventListener() {
                        @Override
                        public int onFileDescriptorEvents(FileDescriptor fd, int events) {
                            // 当收到 Service 的信号时，回调这里
                            handleEvent();
                            // 返回 EVENT_INPUT 表示继续监听，返回 0 表示移除监听
                            return EVENT_INPUT;
                        }
                    }
            );

        } catch (ErrnoException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理数据到达事件
     * 核心逻辑：循环读取 RingBuffer 直到追上 write_pos
     */
    private void handleEvent() {
        try {
            // A. 必须消费 eventfd 的计数 (8 bytes)，否则 epoll 会一直触发
            byte[] buf = new byte[8];
            if (mEventStream.read(buf) != 8) return;

            // B. 获取服务端最新的写位置
            // getInt 是非原子操作，但在 Android 内存模型下配合 EventFd 的 happen-before 关系通常是安全的
            int serverWritePos = mMapping.getInt(OFFSET_WRITE_POS);

            // C. 循环读取
            int loopSafety = 0; // 防止死循环的保险丝

            while (mLocalReadPos != serverWritePos) {
                // 熔断保护：如果循环次数过多，说明逻辑可能出错，强制同步退出
                if (loopSafety++ > 5000) {
                    Log.w(TAG, "Loop safety triggered. Forcing sync.");
                    mLocalReadPos = serverWritePos;
                    break;
                }

                // --- 边界检查 1: 物理空间不足读取头部 ---
                // 如果当前位置连 4 字节的长度头都读不了，说明已经回绕了
                if (mLocalReadPos + 4 > mCapacity) {
                    mLocalReadPos = 0; // 回绕到头部
                    continue; // 重新开始循环
                }

                // 读取数据长度
                int msgLen = mMapping.getInt(HEADER_SIZE + mLocalReadPos);

                // --- 修复点 1: Padding Protocol (填充协议) ---
                // 如果长度为 0，说明这是一个填充包，后面是废弃区域
                if (msgLen == 0) {
                    mLocalReadPos = 0; // 直接回绕到头部
                    continue;
                }

                // --- 修复点 2: Sanity Check (脏数据熔断) ---
                // 如果读到了离谱的长度 (负数 或 比整个 Buffer 还大)
                // 说明读到了残留的垃圾数据，必须立即停止读取
                if (msgLen < 0 || msgLen > mCapacity) {
                    Log.e(TAG, "Corrupted data detected (len=" + msgLen + "). Resetting consumer.");
                    mLocalReadPos = serverWritePos; // 丢弃当前数据，追上进度
                    break;
                }

                // --- 边界检查 3: 逻辑空间越界 ---
                // 即使 msgLen 合法，但如果加上它超出了 Buffer 末尾
                // (这种情况理论上应该被 Padding 协议拦截，这里做双重保险)
                if (mLocalReadPos + 4 + msgLen > mCapacity) {
                    mLocalReadPos = 0;
                    continue;
                }

                // 准备读取 Payload
                mLocalReadPos += 4; // 跳过头部长度
                byte[] dataBytes = new byte[msgLen];

                // 使用 duplicate() 创建视图，避免修改 mMapping 全局的 position
                ByteBuffer readBuf = mMapping.duplicate();
                readBuf.order(ByteOrder.LITTLE_ENDIAN);

                try {
                    readBuf.position(HEADER_SIZE + mLocalReadPos);

                    // --- 修复点 3: 异常捕获 (终极防线) ---
                    // 防止底层 BufferUnderflow 导致 App 闪退
                    readBuf.get(dataBytes);

                    // 回调给上层
                    if (mListener != null) {
                        mListener.onMessage(new String(dataBytes));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Buffer read error: " + e.getMessage());
                    // 发生异常，强制同步，跳过这段坏数据
                    mLocalReadPos = serverWritePos;
                    break;
                }

                // 移动本地指针
                mLocalReadPos += msgLen;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
