package www.andysong.com.basepro.core.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.mingle.widget.ShapeLoadingDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tapadoo.alerter.Alerter;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import www.andysong.com.basepro.R;


/**
 * BaseActivity
 * Created by andysong on 2018/1/16.
 */

public abstract class BaseActivity extends RxAppCompatActivity implements ISupportActivity {
    protected Activity mContext;
    private Unbinder mUnBinder;
    final SupportActivityDelegate mDelegate = new SupportActivityDelegate(this);
    private String mDefaultLoadingString = "加载中...";
    private ShapeLoadingDialog mDialog;
    public SystemBarTintManager mSystemBarTintManager;
    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return mDelegate;
    }

    @Override
    public ExtraTransaction extraTransaction() {
        return mDelegate.extraTransaction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
        mContext = this;
        if (getLayout()!=0)
        {
            setContentView(getLayout());
        }
        initSystemBar(mContext);
        mUnBinder = ButterKnife.bind(this);
        initEventAndData(savedInstanceState);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDelegate.onPostCreate(savedInstanceState);
    }

    private void initSystemBar(Activity activity) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mSystemBarTintManager = new SystemBarTintManager(activity);
        mSystemBarTintManager.setStatusBarTintEnabled(true);
        mSystemBarTintManager.setStatusBarTintResource(R.color.transparent);
    }

    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mDelegate.getFragmentAnimator();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mDelegate.setFragmentAnimator(fragmentAnimator);
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Override
    public void post(Runnable runnable) {
        mDelegate.post(runnable);
    }

    @Override
    final public void onBackPressed() {
        mDelegate.onBackPressed();
    }

    @Override
    public void onBackPressedSupport() {
        mDelegate.onBackPressedSupport();
    }

    @Override
    protected void onDestroy() {
        mDelegate.onDestroy();
        super.onDestroy();
        mUnBinder.unbind();
    }

    public void loadRootFragment(int containerId, @NonNull ISupportFragment toFragment) {
        mDelegate.loadRootFragment(containerId, toFragment);
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    public void loadMultipleRootFragment(int containerId, int showPosition, ISupportFragment... toFragments) {
        mDelegate.loadMultipleRootFragment(containerId, showPosition, toFragments);
    }

    public void start(ISupportFragment toFragment) {
        mDelegate.start(toFragment);
    }

    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mDelegate.start(toFragment, launchMode);
    }

    public void pop() {
        mDelegate.pop();
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    public ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }


    public void showWaitingDialog(String message, DialogInterface.OnCancelListener listener) {
        if (message == null) {
            message = mDefaultLoadingString;
        }
        mDialog = new ShapeLoadingDialog(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setLoadingText(message);
        mDialog.show();
    }

    public void dismissWaitingDialog() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    public void showErrorMsg(String msg, int time) {
//        SnackbarUtil.show(((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0), msg);
        Alerter.create(this)
                .setTitle("提示：")
                .setText(msg)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(time)
                .show();
    }

    /**
     * 将网络请求绑定到生命周期
     *
     * @return
     */
    public LifecycleTransformer getLifecycleTransformer() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }

    /**
     * 展示加载框，传入网络控制器
     *
     * @param message
     * @param disposable
     */
    public void showWaitingDialog(String message, final Disposable disposable) {
        showWaitingDialog(message, dialog -> {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        });
    }

    protected abstract int getLayout();
    protected abstract void initEventAndData(Bundle savedInstanceState);
}
