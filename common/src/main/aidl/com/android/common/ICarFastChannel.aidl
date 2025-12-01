// ICarFastChannel.aidl
package com.android.common;

// Declare any non-default types here with import statements
import com.android.common.bean.FastChannelFd;

interface ICarFastChannel {
    FastChannelFd openFastChannel();
}