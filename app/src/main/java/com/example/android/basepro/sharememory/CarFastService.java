package com.example.android.basepro.sharememory;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.system.ErrnoException;
import android.util.Log;

import com.android.common.IIpcService;
import com.android.common.LibIPC;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CarFastService extends Service {
    private static final String TAG = "IPC_Service";
    private static final int MEM_SIZE = 1024 * 1024; // 1MB 共享内存

    private SharedMemory mSharedMemory;
    private LibIPC mLibIPC;
    private int mEventFd; // 通知句柄
    private AtomicInteger mCounter = new AtomicInteger(0);
    private boolean mRunning = true;



    @Override
    public void onCreate() {
        super.onCreate();
        mLibIPC = new LibIPC();

        try {
            // 1. 创建 Android 匿名共享内存 (API 27+)
            mSharedMemory = SharedMemory.create("MySharedMem", MEM_SIZE);

            // 2. 获取内存 FD
            FileDescriptor memFd = mSharedMemory.getFileDescriptor();
            // 反射获取 int 类型的 fd，因为 mmap 需要 int
            int intMemFd = getFdFromClass(memFd);

            // 3. JNI 初始化内存 (Producer 模式)
            mLibIPC.nativeInit(intMemFd, MEM_SIZE, true);

            // 4. JNI 创建 eventfd 用于通知
            mEventFd = mLibIPC.nativeCreateEventFd();

            Log.i(TAG, "Service Created: MemFd=" + intMemFd + ", EventFd=" + mEventFd);

            // 5. 开启一个线程疯狂发送数据
            startProducingData();

        } catch (ErrnoException e) {
            e.printStackTrace();
        }
    }

    // 模拟高频数据生成
    private void startProducingData() {
        new Thread(() -> {
            while (mRunning) {
                try {
                    // 模拟耗时，60FPS
                    Thread.sleep(16);
                    String data = "Data Packet #" + mCounter.getAndIncrement() +
                            " time=" + System.currentTimeMillis();

                    // 写入共享内存 + 触发 eventfd
                    mLibIPC.nativeWriteData(mEventFd, data);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private final IIpcService.Stub mBinder = new IIpcService.Stub() {

        @Override
        public SharedMemory getSharedMemory() throws RemoteException {
            return mSharedMemory;
        }

        @Override
        public ParcelFileDescriptor getEventFd() throws RemoteException {
            // Event FD 本身就是一个 int，使用 fromFd 即可 (nativeCreateEventFd 返回的是 int)
            try {
                ParcelFileDescriptor pfdEvent = ParcelFileDescriptor.fromFd(mEventFd);
                return pfdEvent;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRunning = false;
        if (mSharedMemory != null) {
            mSharedMemory.close();
        }
        // 实际开发中还需要关闭 mEventFd，可以通过 JNI close(fd)
    }

    // 辅助工具：从 FileDescriptor 对象获取 int fd
    private int getFdFromClass(FileDescriptor fdObj) {
        try {
            java.lang.reflect.Field field = FileDescriptor.class.getDeclaredField("descriptor");
            field.setAccessible(true);
            return field.getInt(fdObj);
        } catch (Exception e) {
            return -1;
        }
    }
}
