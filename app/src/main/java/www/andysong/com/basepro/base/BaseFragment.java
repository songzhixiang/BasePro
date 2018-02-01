package www.andysong.com.basepro.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.tapadoo.alerter.Alerter;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragmentDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import www.andysong.com.basepro.R;

/**
 * BaseFragment
 * Created by andysong on 2018/1/16.
 */

public abstract class BaseFragment extends RxFragment implements ISupportFragment {

    final SupportFragmentDelegate mDelegate = new SupportFragmentDelegate(this);
    protected FragmentActivity _mActivity;
    private Unbinder mUnBinder;
    protected View mView;
    protected BaseActivity mActivity;
    private static final int STATE_MAIN = 0x00;
    private static final int STATE_LOADING = 0x01;
    private static final int STATE_ERROR = 0x02;
    private static final int STATE_EMPTY = 0x03;
    protected Bundle savedInstanceState;
    private View viewError;
    private View viewLoading;
    private View viewEmpty;
    private ViewGroup viewMain;
    private ViewGroup mParent;

    private int mErrorResource = R.layout.view_loading_error;


    private int mEmptyResource = R.layout.view_loading_empty;

    private int currentState = STATE_MAIN;

    private boolean isErrorViewAdded = false;


    private boolean isEmptyViewAdded = false;

    @Override
    public SupportFragmentDelegate getSupportDelegate() {
        return mDelegate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), null);
        initEventAndData(mView);
        return mView;
    }

    /**
     * 将网络请求绑定到生命周期
     *
     * @return
     */
    public LifecycleTransformer getLifecycleTransformer() {
        return bindUntilEvent(FragmentEvent.DESTROY);
    }

    @Override
    public ExtraTransaction extraTransaction() {
        return mDelegate.extraTransaction();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDelegate.onAttach(activity);
        mActivity = (BaseActivity) activity;
        _mActivity = mDelegate.getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return mDelegate.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDelegate.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDelegate.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDelegate.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDelegate.onPause();
    }

    @Override
    public void onDestroyView() {
        mDelegate.onDestroyView();
        super.onDestroyView();
        mUnBinder.unbind();
        hideSoftInput();
    }

    @Override
    public void onDestroy() {
        mDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mDelegate.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mDelegate.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void enqueueAction(Runnable runnable) {
        mDelegate.enqueueAction(runnable);
    }

    @Override
    public void post(Runnable runnable) {
        mDelegate.post(runnable);
    }

    @Override
    public void onEnterAnimationEnd(@Nullable Bundle savedInstanceState) {
        mDelegate.onEnterAnimationEnd(savedInstanceState);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        mDelegate.onLazyInitView(savedInstanceState);
    }

    @Override
    public void onSupportVisible() {
        mDelegate.onSupportInvisible();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onSupportInvisible() {
        mDelegate.onSupportInvisible();
    }

    @Override
    final public boolean isSupportVisible() {
        return mDelegate.isSupportVisible();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return mDelegate.onCreateFragmentAnimator();

    }

    public void showErrorMsg(String msg, int time) {
//        SnackbarUtil.show(((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0), msg);
        Alerter.create(mActivity)
                .setTitle("提示：")
                .setText(msg)
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(time)
                .show();
    }

    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mDelegate.getFragmentAnimator();
    }

    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mDelegate.setFragmentAnimator(fragmentAnimator);
    }


    @Override
    public void setFragmentResult(int resultCode, Bundle bundle) {
        mDelegate.setFragmentResult(resultCode, bundle);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        mDelegate.onFragmentResult(requestCode, resultCode, data);
    }

    @Override
    public void onNewBundle(Bundle args) {
        mDelegate.onNewBundle(args);
    }

    @Override
    public void putNewBundle(Bundle newBundle) {
        mDelegate.putNewBundle(newBundle);
    }

    @Override
    public boolean onBackPressedSupport() {
        return mDelegate.onBackPressedSupport();
    }

    protected void hideSoftInput() {
        mDelegate.hideSoftInput();
    }

    protected void showSoftInput(final View view) {
        mDelegate.showSoftInput(view);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment) {
        mDelegate.loadRootFragment(containerId, toFragment);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnim) {
        mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnim);
    }

    public void start(ISupportFragment toFragment) {
        mDelegate.start(toFragment);
        hideSoftInput();
    }

    public void start(final ISupportFragment toFragment, @LaunchMode int launchMode) {
        mDelegate.start(toFragment, launchMode);
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        mDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * Start the target Fragment and pop itself
     */
    public void startWithPop(ISupportFragment toFragment) {
        mDelegate.startWithPop(toFragment);
    }

    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        mDelegate.replaceFragment(toFragment, addToBackStack);
    }

    public void pop() {
        mDelegate.pop();
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * Pop the child fragment.
     */
    public void popChild() {
        mDelegate.popChild();
    }

    /**
     * 获取栈内的fragment对象
     */
    public <T extends ISupportFragment> T findChildFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getChildFragmentManager(), fragmentClass);
    }


    protected abstract int getLayoutId();

    protected void initEventAndData(View mView) {
        if (getView() == null)
            return;
        viewMain = (ViewGroup) getView().findViewById(R.id.swiplayout);
        if (viewMain == null) {
            throw new IllegalStateException(
                    "The subclass of RootActivity must contain a View named 'view_main'.");
        }
        if (!(viewMain.getParent() instanceof ViewGroup)) {
            throw new IllegalStateException(
                    "view_main's ParentView should be a ViewGroup.");
        }
        mParent = (ViewGroup) viewMain.getParent();
        View.inflate(mActivity, R.layout.view_loading, mParent);
        viewLoading = mParent.findViewById(R.id.fragment_loading);
        viewLoading.setVisibility(View.GONE);
        viewMain.setVisibility(View.VISIBLE);
    }

    /**
     * 系统toast提示
     *
     * @param msg
     */
    public void showToastMsg(String msg,int time) {
        mActivity.showErrorMsg(msg,time);
    }

    /**
     * 显示错误视图
     */
    public void stateError() {
        if (currentState == STATE_ERROR)
            return;
        if (!isErrorViewAdded) {
            isErrorViewAdded = true;
            View.inflate(mActivity, mErrorResource, mParent);
            viewError = mParent.findViewById(R.id.globalError);
            if (viewError == null) {
                throw new IllegalStateException(
                        "A View should be named 'view_error' in ErrorLayoutResource.");
            }
        }
        hideCurrentView();
        currentState = STATE_ERROR;
        viewError.setVisibility(View.VISIBLE);
    }

    /**
     * 显示空视图
     */
    public void stateEmpty() {
        if (currentState == STATE_EMPTY)
            return;
        if (!isEmptyViewAdded) {
            isEmptyViewAdded = true;
            View.inflate(mActivity, mEmptyResource, mParent);
            viewEmpty = mParent.findViewById(R.id.globalEmpty);
            if (viewEmpty == null) {
                throw new IllegalStateException(
                        "A View should be named 'view_error' in ErrorLayoutResource.");
            }
        }
        hideCurrentView();
        currentState = STATE_EMPTY;
        viewEmpty.setVisibility(View.VISIBLE);

    }


    /**
     * 显示加载视图
     */
    public void stateLoading() {
        if (currentState == STATE_LOADING)
            return;
        hideCurrentView();
        currentState = STATE_LOADING;
        viewLoading.setVisibility(View.VISIBLE);

    }


    /**
     * 显示内容
     */
    public void stateMain() {
        if (currentState == STATE_MAIN)
            return;
        hideCurrentView();
        currentState = STATE_MAIN;
        viewMain.setVisibility(View.VISIBLE);
    }

    private void hideCurrentView() {
        switch (currentState) {
            case STATE_MAIN:
                viewMain.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                viewLoading.setVisibility(View.GONE);
                break;
            case STATE_ERROR:
                if (viewError != null) {
                    viewError.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void setErrorResource(int errorLayoutResource) {
        this.mErrorResource = errorLayoutResource;
    }


    /**
     * 打开加载弹出框
     *
     * @param message  加载信息
     * @param listener OnCancelListener 一般用于在手动关闭时停止当前联网
     */
    public void showWaitingDialog(String message, DialogInterface.OnCancelListener listener) {
        mActivity.showWaitingDialog(message, listener);
    }

    public void showWaitingDialog(String message, final Disposable disposable) {
        mActivity.showWaitingDialog(message, disposable);
    }

    public void dismissWaitingDialog() {
        mActivity.dismissWaitingDialog();
    }

}
