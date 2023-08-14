package com.example.android.basepro.task;

/**
 * @author andysong
 * @data 2019-07-03
 * @discription xxx
 */
public abstract class MainTask extends Task {

    @Override
    public boolean runOnMainThread() {
        return true;
    }
}
