package com.android.common;

public class LibIPC {
    static {
        System.loadLibrary("ipcdemo");
    }

    // 回调接口
    public interface DataListener {
        void onMessage(String msg);
    }

    private DataListener listener;

    public void setListener(DataListener listener) {
        this.listener = listener;
    }

    // 由 Native 线程调用
    public void onDataReceived(String msg) {
        if (listener != null) {
            listener.onMessage(msg);
        }
    }

    /**
     * 初始化共享内存映射
     * @param fd 共享内存的文件描述符
     * @param size 大小
     * @param isProducer 是否是生产者 (Service端为true)
     */
    public native void nativeInit(int fd, int size, boolean isProducer);

    /**
     * 创建一个用于通知的 eventfd (类似 socketpair)
     */
    public native int nativeCreateEventFd();

    /**
     * 写入数据并触发通知
     */
    public native void nativeWriteData(int eventFd, String data);

    /**
     * 开始 Epoll 监听
     */
    public native void nativeStartListen(int eventFd);
}