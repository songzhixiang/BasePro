package com.example.android.basepro.sharememory;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.common.ICarFastChannel;
import com.android.common.IIpcService;
import com.android.common.LibIPC;
import com.example.android.basepro.R;

import java.util.List;

/**
 * Copyright (C) ,2016-2025,Write by AndySong
 *
 * @PackageName : com.example.android.basepro.sharememory
 * @Description : TODO
 * @Author : SongZhiXiang
 * @Date : 2025/11/25 15:53
 * @Version : 1.0
 */
public class CarClientActivity extends AppCompatActivity {

    private IIpcService mRemoteService;
    private LibIPC mLibIPC;
    // 在 MainActivity 中
    private PureJavaClient mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);


        mLibIPC = new LibIPC();


        // 绑定 Service

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarClientActivity.this, CarFastService.class);
                bindService(intent, mConnection, BIND_AUTO_CREATE);
            }
        });


        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置回调，当 Native 层 epoll 唤醒并读到数据时调用
                mLibIPC.setListener(msg -> {
                    Log.e("Andy", "Received: " + msg);
                });
            }
        });
    }


    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("Andy", "回调线程 " + Thread.currentThread().getName());
            mRemoteService = IIpcService.Stub.asInterface(service);
            try {
                // 直接获取对象
                SharedMemory mem = mRemoteService.getSharedMemory();
                ParcelFileDescriptor eventFd = mRemoteService.getEventFd();
                // 1. 通过 Binder 获取 FD 句柄 (只发生一次)
                //way1 : 通过jni的方式
//                    int memFd = fds.get(0).getFd();
//                    int eventFd = fds.get(1).getFd();
//
//                    Log.i("IPC_Client", "Got FDs: Mem=" + memFd + " Event=" + eventFd);
//
//                    // 2. 初始化 Consumer 端的内存映射
//                    // 注意：必须 detachfd 或者是 dup 出来的，否则 Java GC 可能关闭它
//                    mLibIPC.nativeInit(memFd, 1024 * 1024, false);
//
//                    // 3. 启动 Epoll 监听
//                    mLibIPC.nativeStartListen(eventFd);


                //way2 : 通过纯java的方式
                // 初始化纯 Java 客户端
                mClient = new PureJavaClient();
                mClient.setListener(msg -> {
                    // 已经在主线程回调了（如果 connect 是在主线程调用的）
                    Log.e("Andy", "Java Client Recv: " + msg);
                });

                // 开始工作
                mClient.connect(mem, eventFd);

                Toast.makeText(CarClientActivity.this, "IPC Connected!", Toast.LENGTH_SHORT).show();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
        }
    };
}
