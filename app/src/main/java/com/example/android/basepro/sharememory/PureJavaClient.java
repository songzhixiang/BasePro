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
    private static final int HEADER_SIZE = 16;
    private static final int OFFSET_WRITE_POS = 0;
    private static final int OFFSET_DATA_SIZE = 8;
    private static final int OFFSET_MAGIC = 12;

    private SharedMemory mSharedMemory;
    private ByteBuffer mMapping;
    private ParcelFileDescriptor mPfdEvent;
    private FileInputStream mEventStream; // 用于读取 eventfd 的 8字节计数

    private int mCapacity;
    private int mLocalReadPos = 0; // 客户端自己维护读取进度，支持多客户端互不干扰

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
    public void connect(SharedMemory mSharedMemory, ParcelFileDescriptor pfdEvent) {
        try {
            mPfdEvent = pfdEvent;


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
            Log.i(TAG, "Connected! Capacity: " + mCapacity);

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

    private void handleEvent() {
        try {
            // A. 必须消费掉 eventfd 的计数 (Read 8 bytes)
            // 相当于 C++ 的 read(fd, &u, 8)
            byte[] buf = new byte[8];
            int read = mEventStream.read(buf);
            if (read != 8) return;

            // B. 读取共享内存数据
            // 获取服务端当前的写位置
            int serverWritePos = mMapping.getInt(OFFSET_WRITE_POS);

            // 简单的 RingBuffer 追赶逻辑
            while (mLocalReadPos != serverWritePos) {
                // 1. 读取数据长度
                int msgLenPos = HEADER_SIZE + mLocalReadPos;
                if (msgLenPos + 4 > HEADER_SIZE + mCapacity) {
                    mLocalReadPos = 0; // 回绕
                    msgLenPos = HEADER_SIZE;
                }

                int msgLen = mMapping.getInt(msgLenPos);
                mLocalReadPos += 4; // 跳过长度头
                if (mLocalReadPos >= mCapacity) mLocalReadPos = 0;

                // 2. 读取实际数据
                byte[] dataBytes = new byte[msgLen];

                // 处理数据可能跨越缓冲区末尾的情况 (RingBuffer Wrap)
                // 为简化 Demo，假设数据是连续写入未切分的 (对应 Service 端逻辑)
                // 严谨实现需要处理 msgLen + pos > capacity 的情况

                int dataPos = HEADER_SIZE + mLocalReadPos;
                // 注意：ByteBuffer 的 get(byte[]) 方法是从当前 position 读取，
                // 我们需要绝对位置读取，或者 duplicate 一个 buffer 设置 position
                // 这里使用绝对位置逐个拷贝或者 duplicate 方式

                ByteBuffer readBuf = mMapping.duplicate();
                readBuf.order(ByteOrder.LITTLE_ENDIAN);
                readBuf.position(dataPos);
                readBuf.get(dataBytes);

                mLocalReadPos += msgLen;
                if (mLocalReadPos >= mCapacity) mLocalReadPos = 0; // 这里的回绕逻辑要和服务端严格一致

                // 3. 回调上层
                String msg = new String(dataBytes);
                if (mListener != null) {
                    mListener.onMessage(msg);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (mSharedMemory != null) {
            mSharedMemory.close();
        }
        // 注意：不要在回调内部 close fd，否则会导致 Looper 崩溃
    }
}
