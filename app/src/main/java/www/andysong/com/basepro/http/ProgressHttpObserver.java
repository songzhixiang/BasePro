package www.andysong.com.basepro.http;

import android.os.UserManager;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import www.andysong.com.basepro.GlobalConfig;
import www.andysong.com.basepro.base.BaseActivity;
import www.andysong.com.basepro.http.parser.ParseException;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/26
 *     desc   : 网络返回监听,带有加载动画
 *     version: 1.0
 * </pre>
 */

public abstract class ProgressHttpObserver<T> extends DefaultHttpObserver<T> {
    /**
     * 是否显示loading
     */
    private boolean isLoading = true;

    public ProgressHttpObserver(BaseActivity baseActivity) {
        super(baseActivity);
        if (GlobalConfig.isTest && baseActivity == null) {
            throw new RuntimeException("HttpObserver has not baseActivity");
        }
    }

    public ProgressHttpObserver(BaseActivity baseActivity, boolean isLoading) {
        super(baseActivity);
        this.isLoading = isLoading;
        if (GlobalConfig.isTest && baseActivity == null) {
            throw new RuntimeException("HttpObserver has not baseActivity");
        }
    }

    @Override
    public void onStart(Disposable disposable) {
        super.onStart(disposable);
        if (mBaseActivity != null && isLoading) {
            mBaseActivity.showWaitingDialog(getLoadingText(), disposable);
        }
    }

    @Override
    public void onNext(T t) {
        //在onsuccess之前隐藏loading，以免出现跳转页面错误的情况
        if (mBaseActivity != null && isLoading) {
            mBaseActivity.dismissWaitingDialog();
        }
        super.onNext(t);
    }

    @Override
    public void onError(@NonNull ParseException e, boolean isLocalError) {
        super.onError(e, isLocalError);
        if (mBaseActivity != null && isLoading) {
            mBaseActivity.dismissWaitingDialog();
            mBaseActivity.showErrorMsg(e.getMessage(),1000);
            if (e.getMessage().indexOf("Token")>0)
            {
//                UserManager.logoutUser();
//                ActivityUtil.next(mBaseActivity, LoginActivity.class);
            }
            if (e.getMessage().indexOf("实名认证")>0){
//                ActivityUtil.next(mBaseActivity, AuthenticationActivity.class,null, 0, -1, -1, false);
            }
        }

    }

    @Override
    public final void onComplete() {
        super.onComplete();
    }

    public String getLoadingText() {
        return null;
    }
}

