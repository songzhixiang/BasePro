package com.example.android.basepro.utils;

import android.util.Log;

/**
 * @author andysong
 * @data 2019-07-03
 * @discription xxx
 */
public class DispatcherLog {

    private static boolean sDebug = true;

    public static void i(String msg) {
        if (!sDebug) {
            return;
        }
        Log.i("task",msg);
    }

    public static boolean isDebug() {
        return sDebug;
    }

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

}
