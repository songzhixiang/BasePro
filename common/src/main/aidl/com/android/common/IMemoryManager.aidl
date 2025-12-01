// IMemoryManager.aidl
package com.android.common;
import com.android.common.IMemoryCallback;
// Declare any non-default types here with import statements

interface IMemoryManager {
    void client2server(in ParcelFileDescriptor pfd);
    void registerCallback(IMemoryCallback callback);
    void unregisterCallback(IMemoryCallback callback);
}