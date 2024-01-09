package com.example.android.basepro.watchdog

import android.os.Handler
import android.os.HandlerThread
import android.os.MessageQueue
import android.os.SystemClock
import android.util.Log

/**
 * Copyright (C) ,2016-2024,Write by AndySong
 * @PackageName : com.android.common.watchdog
 * @Description : TODO
 * @Author : SongZhiXiang
 * @Date : 2024/1/9 15:29
 * @Version : 1.0
 */
const val DEFAULT_WAIT_TIME_MILLIS = 5000
const val COMPLETE = 0
const val WAITING = 1
const val WAITING_HALF = 2
const val TIME_OVER = 3
const val TAG = "WatchDog"

class HandlerChecker(var handler: Handler) : Runnable {

    var monitors: ArrayList<Monitor> = ArrayList()
    var completed = false
    var maxDuration = DEFAULT_WAIT_TIME_MILLIS
    var startTime = 0L


    init {
        completed = true
    }

    override fun run() {
        for (item in monitors) {
            item.monitor() // 阻塞
        }
        completed = true
    }

    fun addMonitor(monitor: Monitor) {
        monitors.add(monitor)
    }

    fun scheduleCheckLocked() {
        val isPolling = MessageQueue::class.java.getMethod("isPolling")
            .invoke(handler.looper.queue) as Boolean
        if (monitors.size == 0 && isPolling) {
            completed = true
            return
        }
        if (!completed) return
        completed = false
        startTime = SystemClock.uptimeMillis()
        handler.postAtFrontOfQueue(this)
    }

    fun getCompletionStateLocked(): Int {
        if (completed) {
            return COMPLETE
        } else {
            val diff = SystemClock.uptimeMillis() - startTime
            if (diff < maxDuration / 2) {
                return WAITING_HALF
            } else if (diff < maxDuration) {
                return WAITING
            }
        }
        return TIME_OVER
    }

}


object WatchDog : Thread("WatchDog") {
    private val handlerChecker: ArrayList<HandlerChecker> = ArrayList()
    private var monitorChecker: HandlerChecker

    init {
        val monitorHandlerThread = HandlerThread("MonitorHandlerThread")
        monitorHandlerThread.start()
        val tempMonitorHandler = Handler(monitorHandlerThread.looper)


        monitorChecker = HandlerChecker(tempMonitorHandler)
        handlerChecker.add(monitorChecker)
    }

    fun addMonitor(monitor: Monitor) {
        monitorChecker.addMonitor(monitor)
    }

    override fun run() {
        super.run()
        while (true) {
            for (item in handlerChecker) {
                item.scheduleCheckLocked()
            }
            SystemClock.sleep(500)
            when (evaluateCheckerCompletionLocked()) {
                COMPLETE -> {
                    continue
                }

                WAITING -> {
                    Log.e(TAG, "time ")
                    continue
                }

                WAITING_HALF -> {
                    Log.e(TAG, "time half")
                    continue
                }
            }
            Log.e(TAG, "time overdue")
        }
    }

    private fun evaluateCheckerCompletionLocked(): Int {
        var state = COMPLETE
        for (item in handlerChecker) {
            state = state.coerceAtLeast(item.getCompletionStateLocked())
        }
        return state
    }
}