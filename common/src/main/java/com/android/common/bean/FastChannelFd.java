package com.android.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.ParcelFileDescriptor;

public class FastChannelFd implements Parcelable {
    public ParcelFileDescriptor socketFd;
    public ParcelFileDescriptor sharedMemFd;

    public FastChannelFd(ParcelFileDescriptor socketFd,
                         ParcelFileDescriptor sharedMemFd) {
        this.socketFd = socketFd;
        this.sharedMemFd = sharedMemFd;
    }

    protected FastChannelFd(Parcel in) {
        socketFd = in.readParcelable(ParcelFileDescriptor.class.getClassLoader());
        sharedMemFd = in.readParcelable(ParcelFileDescriptor.class.getClassLoader());
    }

    public static final Creator<FastChannelFd> CREATOR = new Creator<FastChannelFd>() {
        @Override
        public FastChannelFd createFromParcel(Parcel in) {
            return new FastChannelFd(in);
        }

        @Override
        public FastChannelFd[] newArray(int size) {
            return new FastChannelFd[size];
        }
    };

    @Override
    public int describeContents() {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(socketFd, flags);
        dest.writeParcelable(sharedMemFd, flags);
    }
}
