package com.example.android.basepro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.android.basepro.watchdog.DownLoadThread
import com.example.android.basepro.watchdog.WatchDog


class MainActivity : AppCompatActivity() {

    lateinit var commonTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        commonTextView = findViewById(R.id.tv_common_msg)

        findViewById<Button>(R.id.btn_watchdog).setOnClickListener {
            //WatchDog
            val downloadThread = DownLoadThread()
            downloadThread.start()

            downloadThread.uiHandler = DownloadUIHandler(Looper.getMainLooper())

            WatchDog.addMonitor(downloadThread)
            WatchDog.start()

            downloadThread.startDownload()


        }
    }

    inner class DownloadUIHandler(looper:Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                DownLoadThread.STATE_DOWNLOADING ->{
                    commonTextView.text  = "下载中 ${msg.obj as Int}"
                    Log.e(TAG,"STATE_DOWNLOADING ..")
                }
                DownLoadThread.STATE_START ->{
                    Log.e(TAG,"STATE_START ..")
                }
                DownLoadThread.STATE_FINISH ->{
                    Log.e(TAG,"STATE_FINISH ..")
                }
            }
        }
    }

    companion object {
        val TAG = "MainActivity"
    }
}