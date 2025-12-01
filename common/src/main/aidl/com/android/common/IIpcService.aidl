// ICarFastChannel.aidl
package com.android.common;

import android.os.ParcelFileDescriptor;

interface IIpcService {
    /**
     * 直接返回 SharedMemory 对象
     * Framework 会自动处理 FD 的传递和对象的重建
     */
    SharedMemory getSharedMemory();

    /**
     * 获取通知用的 EventFD
     */
    ParcelFileDescriptor getEventFd();
}