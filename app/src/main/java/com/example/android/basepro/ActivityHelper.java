package com.example.android.basepro;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 *
 * Created by andysong on 2018/1/16.
 */

public class ActivityHelper implements Application.ActivityLifecycleCallbacks {



    public ActivityHelper() {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        ActivityManager.getInstance().addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityManager.getInstance().finishActivity(activity);
    }
}
