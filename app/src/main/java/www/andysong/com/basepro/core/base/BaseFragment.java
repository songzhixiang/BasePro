package www.andysong.com.basepro.core.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tapadoo.alerter.Alerter;
import com.trello.rxlifecycle2.LifecycleTransformer;
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

public abstract class BaseFragment extends RxFragment implements ISupportFragment,View.OnClickListener {

    final SupportFragmentDelegate mDelegate = new SupportFragmentDelegate(this);
    protected FragmentActivity _mActivity;
    private Unbinder mUnBinder;
    protected View mView;
    protected BaseActivity mActivity;
    protected boolean isInited = false;


    @Override
    public SupportFragmentDelegate getSupportDelegate() {
        return mDelegate;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), null);
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

    public void onClick(View v) {
//        if (UiUtils.isFastDoubleClick()) return;
        switch (v.getId()) {
            case R.id.banner_back:
//                onBackPressed();
                break;
        }
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
        isInited = true;
        initEventAndData(mView);
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

    /**
     * 设置头部
     *
     * @param backImage
     * @param title
     * @param operate1Image
     * @param listener
     */
    public void setHeader(int backImage, String title, int operate1Image, View.OnClickListener listener) {
        ImageView banner_back = mView.findViewById(R.id.banner_back);
        TextView banner_title = mView.findViewById(R.id.banner_title);
        ImageView banner_right_image = mView.findViewById(R.id.banner_right_image);
        if (banner_back != null) {
            if (backImage == -1) {
                banner_back.setVisibility(View.GONE);
            } else if (backImage == 0) {
                banner_back.setImageResource(R.drawable.back_icon);
                banner_back.setVisibility(View.VISIBLE);
                banner_back.setOnClickListener(listener);
            } else {
                banner_back.setImageResource(backImage);
                banner_back.setVisibility(View.VISIBLE);
                banner_back.setOnClickListener(listener);
            }
        }
        if (banner_title != null) {
            if (!TextUtils.isEmpty(title)) {
                banner_title.setVisibility(View.VISIBLE);
                banner_title.setText(title);
            } else {
                banner_title.setVisibility(View.GONE);
            }
        }
        if (banner_right_image != null) {
            if (operate1Image != -1) {
                banner_right_image.setVisibility(View.VISIBLE);
                banner_right_image.setOnClickListener(listener);
                banner_right_image.setImageResource(operate1Image);
            } else {
                banner_right_image.setVisibility(View.GONE);
            }
        }
    }

    public void setToolbarEnable(boolean enable) {
        if (!enable) {
            mView.findViewById(R.id.banner_bar).setVisibility(View.GONE);
        }
    }

    /**
     * 设置头部
     *
     * @param backImage
     * @param title
     * @param rightStr
     * @param listener
     */
    public void setHeader(int color, int backImage, String title, String rightStr, View.OnClickListener listener) {
        LinearLayout banner_bar = mView.findViewById(R.id.banner_bar);
        ImageView banner_back = mView.findViewById(R.id.banner_back);
        TextView banner_title = mView.findViewById(R.id.banner_title);
        TextView banner_right_text = mView.findViewById(R.id.banner_right_text);
        if (color!=0)
        {
            banner_bar.setBackgroundColor(color);
        }

        if (banner_back != null) {
            if (backImage == -1) {
                banner_back.setVisibility(View.GONE);
            } else if (backImage == 0) {
                banner_back.setImageResource(R.drawable.back_icon);
                banner_back.setVisibility(View.VISIBLE);
                banner_back.setOnClickListener(listener);
            } else {
                banner_back.setImageResource(backImage);
                banner_back.setVisibility(View.VISIBLE);
                banner_back.setOnClickListener(listener);
            }
        }
        if (banner_title != null) {
            if (!TextUtils.isEmpty(title)) {
                banner_title.setVisibility(View.VISIBLE);
                banner_title.setText(title);
            } else {
                banner_title.setVisibility(View.GONE);
            }
        }
        if (banner_right_text != null) {
            if (!TextUtils.isEmpty(rightStr)) {
                banner_right_text.setVisibility(View.VISIBLE);
                banner_right_text.setOnClickListener(listener);
                banner_right_text.setText(rightStr);
            } else {
                banner_right_text.setVisibility(View.GONE);
            }
        }
    }

    public void showErrorMsg(String msg, int time) {
//        SnackbarUtil.show(((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0), msg);
        Alerter.create(_mActivity)
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

    protected abstract void initEventAndData(View mView);

    /**
     * 系统toast提示
     *
     * @param msg
     */
    public void showToastMsg(String msg, int time) {
        mActivity.showErrorMsg(msg, time);
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
