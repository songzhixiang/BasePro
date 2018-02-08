package www.andysong.com.basepro.http;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import www.andysong.com.basepro.GlobalConfig;
import www.andysong.com.basepro.core.base.BaseFragment;
import www.andysong.com.basepro.http.parser.ParseException;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/26
 *     desc   : 网络返回监听
 *     version: 1.0
 * </pre>
 */

public abstract class FragmentStateHttpObserver<T> extends DefaultHttpObserver<T>{
    /**
     * 是否显示loading
     */
    private boolean isLoading = true;

    public FragmentStateHttpObserver(BaseFragment baseFragment) {
        super(baseFragment);
        if (GlobalConfig.isTest && baseFragment == null) {
            throw new RuntimeException("HttpObserver has not baseFragment");
        }
    }

    public FragmentStateHttpObserver(BaseFragment baseFragment, boolean isLoading) {
        super(baseFragment);
        this.isLoading = isLoading;
        if (GlobalConfig.isTest && baseFragment == null) {
            throw new RuntimeException("HttpObserver has not baseActivity");
        }
    }

    @Override
    public void onStart(Disposable disposable) {
        super.onStart(disposable);
        if (mBaseFragment != null && isLoading) {
//            mBaseFragment.stateLoading();
        }
    }

    @Override
    public void onError(@NonNull ParseException e, boolean isLocalError) {
        super.onError(e, isLocalError);
        if (mBaseFragment != null && isLoading) {
//            mBaseFragment.stateError();
            mBaseFragment.showToastMsg(e.getMessage(),1000);
        }
    }


    public void onNext(T t) {
        super.onNext(t);
        if (mBaseFragment != null) {
//            mBaseFragment.stateMain();
        }
    }

    @Override
    public final void onComplete() {
        super.onComplete();
    }
}
