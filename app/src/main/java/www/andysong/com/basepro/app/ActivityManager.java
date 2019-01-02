package www.andysong.com.basepro.app;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

/**
 * Activity的维护栈
 * @author AndySong on 2019/1/2
 * @Blog https://github.com/songzhixiang
 */
public class ActivityManager {
    /**
     * 手动维护的activity栈
     */
    private Stack<WeakReference<Activity>> mActivityStack;

    private static class ActivityManageHolder{
        public  static   final  ActivityManager INSTANCE = new ActivityManager();
    }
    private ActivityManager() {}

    public static ActivityManager getInstance(){
        return ActivityManageHolder.INSTANCE;
    }

    /**
     * 获取栈内存中Activity的个数
     * @return
     */
    public int getActivityCount() {
        return mActivityStack != null ? mActivityStack.size() : 0;
    }

    /**
     * 添加Activity到栈
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(new WeakReference<>(activity));
    }


    /**
     * 关闭指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && mActivityStack != null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity temp = activityReference.get();
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove();
                    continue;
                }
                if (temp == activity) {
                    it.remove();
                }
            }
            activity.finish();
        }
    }

    /**
     * 关闭指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        if (mActivityStack != null && cls!=null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity activity = activityReference.get();
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove();
                    continue;
                }
                if (activity.getClass().equals(cls)) {
                    it.remove();
                    activity.finish();
                }
            }
        }
    }

    public void finshActivities(Class<? extends Activity>... activityClasses) {
        for(Class<?>  cls :activityClasses){
            if (mActivityStack != null && cls!=null) {
                // 使用迭代器进行安全删除
                for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                    WeakReference<Activity> activityReference = it.next();
                    Activity activity = activityReference.get();
                    // 清理掉已经释放的activity
                    if (activity == null) {
                        it.remove();
                        continue;
                    }
                    if (activity.getClass().equals(cls)) {
                        it.remove();
                        activity.finish();
                    }
                }
            }
        }

    }

    /**
     * 关闭指定Activity之外的所有Activity
     *
     * @param cls
     */
    public void finishAllActivityExcept(Class<?> cls) {
        if (cls != null && mActivityStack != null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity temp = activityReference.get();
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove();
                    continue;
                }
                if (!temp.getClass().equals(cls)) {
                    it.remove();
                    temp.finish();
                }
            }
        }
    }


    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (mActivityStack != null) {
            for (WeakReference<Activity> activityReference : mActivityStack) {
                Activity activity = activityReference.get();
                if (activity != null) {
                    activity.finish();
                }
            }
            mActivityStack.clear();
        }
    }

    /**
     * 销毁栈顶的activity
     */
    public void finishTopActivity() {
        WeakReference<Activity> activity = mActivityStack.lastElement();
        if (activity != null && activity.get() != null) {
            activity.get().finish();

            mActivityStack.remove(activity);
        }
    }


    /**
     * 获取当前Activity（栈中最后一个压入的）
     *
     * @return
     */
    public Activity currentActivity() {
        checkWeakReference();
        if (mActivityStack != null && !mActivityStack.isEmpty()) {
            return mActivityStack.lastElement().get();
        }
        return null;
    }

    /**
     * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
     */
    private void checkWeakReference() {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            for (Iterator<WeakReference<Activity>> it = mActivityStack.iterator(); it.hasNext(); ) {
                WeakReference<Activity> activityReference = it.next();
                Activity temp = activityReference.get();
                if (temp == null) {
                    it.remove();
                }
            }
        }
    }




    /***
     * 通过class 获取栈顶Activity
     *
     * @param cls
     * @return Activity
     */
    public Activity getActivityByClass(Class<?> cls) {
        Activity return_activity = null;
        for (WeakReference<Activity> activity : mActivityStack) {
            if (activity.get().getClass().equals(cls)) {
                return_activity = activity.get();
                break;
            }
        }
        return return_activity;
    }















    /**
     * 退出应用程序
     */
    public void exitApp() {
        try {
            finishAllActivity();
            // 退出JVM,释放所占内存资源,0表示正常退出
            System.exit(0);
            // 从系统中kill掉应用程序
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Stack<WeakReference<Activity>> getStack() {
        return mActivityStack;
    }



//    /**
//     * 跳转到顶层Activity
//     */
//    public void popToTopActivity() {
//        finishAllActivityExcept(S.class);
//    }
//
//
//    /**
//     * 判读应用是否是第一次打开，还是已经运行
//     */
//    public boolean AppAlreadyRunning() {
//        return !mActivityStack.isEmpty() && (mActivityStack.size() > 1 || currentActivity() instanceof MainActivity);
//    }

    /**
     * 是否包含当前activity
     * @param activityClasses
     * @return
     */
    public boolean haveActivity(Class<? extends Activity>... activityClasses){
        if (mActivityStack != null && activityClasses!=null) {
            for (WeakReference<Activity> activityReference : mActivityStack) {
                Activity activity = activityReference.get();
                if( Arrays.asList(activityClasses).contains( activity.getClass() ) ){
                    return true;
                }
            }
        }
        return false;
    }
}
