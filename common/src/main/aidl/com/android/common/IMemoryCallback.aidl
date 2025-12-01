// IMemoryCallback.aidl
package com.android.common;

// Declare any non-default types here with import statements

interface IMemoryCallback {

   void server2Client(in ParcelFileDescriptor pfd);
}