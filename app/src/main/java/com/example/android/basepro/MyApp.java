package com.example.android.basepro;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;




/**
 * Application
 *
 * Created by andysong on 2018/1/16.
 */

public class MyApp extends Application {
    private static MyApp sApplicationContext = null;
    private ActivityHelper mActivityHelper;

    // 获取ApplicationContext
    public static MyApp getContext() {
        return sApplicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationContext = this;
        //初始化数据库
        Utils.init(this);
        mActivityHelper = new ActivityHelper();
        registerActivityLifecycleCallbacks(mActivityHelper);
    }

    public static ActivityHelper getActivityHelper() {
        return ((MyApp) sApplicationContext).mActivityHelper;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


}
