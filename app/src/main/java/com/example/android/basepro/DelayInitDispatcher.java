package com.example.android.basepro;

import android.os.Looper;
import android.os.MessageQueue;

import java.util.LinkedList;
import java.util.Queue;

import com.example.android.basepro.task.DispatchRunnable;
import com.example.android.basepro.task.Task;

/**
 * @author andysong
 * @data 2019-07-03
 * @discription xxx
 */
public class DelayInitDispatcher {
    private Queue<Task> mDelayTasks = new LinkedList<>();

    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            if(mDelayTasks.size()>0){
                Task task = mDelayTasks.poll();
                new DispatchRunnable(task).run();
            }
            return !mDelayTasks.isEmpty();
        }
    };

    public DelayInitDispatcher addTask(Task task){
        mDelayTasks.add(task);
        return this;
    }

    public void start(){
        Looper.myQueue().addIdleHandler(mIdleHandler);
    }
}
