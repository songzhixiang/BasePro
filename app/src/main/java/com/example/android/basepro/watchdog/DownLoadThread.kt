package com.example.android.basepro.watchdog

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.SystemClock
import android.util.Log

/**
 * Copyright (C) ,2016-2024,Write by AndySong
 * @PackageName : com.android.common.watchdog
 * @Description : TODO
 * @Author : SongZhiXiang
 * @Date : 2024/1/9 15:32
 * @Version : 1.0
 */

class DownLoadThread : HandlerThread("DownloadThread"), Monitor {

    companion object {
         val TAG = "DownloadThread"
         val STATE_START = 0
         val STATE_DOWNLOADING = 1
         val STATE_FINISH = 2
    }

    var percent = 0
    var uiHandler:Handler? = null



    override fun onLooperPrepared() {
        while (percent != 100) {
            Log.e(TAG,"下载了 $percent %")
            percent ++
            SystemClock.sleep(1000)
            uiHandler?.sendMessage(Message.obtain().apply {
                what = STATE_DOWNLOADING
                obj = percent
            })
            if (percent == 10) {
                testLock()
            }
        }
        uiHandler?.sendEmptyMessage(STATE_FINISH)
    }

    private fun testLock(){
        synchronized(this) {
            SystemClock.sleep(3000)
        }
    }

    fun startDownload(){
        uiHandler?.sendEmptyMessage(STATE_START)
    }

    override fun monitor() {
        synchronized(this) {
            //阻塞
        }
    }
}