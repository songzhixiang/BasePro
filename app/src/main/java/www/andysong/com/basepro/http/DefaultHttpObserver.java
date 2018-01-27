package www.andysong.com.basepro.http;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;
import www.andysong.com.basepro.base.BaseActivity;
import www.andysong.com.basepro.base.BaseFragment;
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

public abstract class DefaultHttpObserver<T> implements HttpClientApi.HttpObserver<T> {

    public Disposable mDisposable;
    public BaseActivity mBaseActivity;
    public BaseFragment mBaseFragment;

    public DefaultHttpObserver() {
    }

    public DefaultHttpObserver(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    public DefaultHttpObserver(BaseFragment baseFragment) {
        mBaseFragment = baseFragment;
    }

    public void onStart(Disposable disposable) {
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        onStart(d);
    }

    @Override
    public void onNext(T t) {
        if (t != null && t instanceof NoDataResponse) {
            onSuccess(null);
        } else {
            onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof IOException ||
                (e instanceof ParseException && "local".equals(((ParseException) e).SERVER_ERROR_CODE))) {
            if (e instanceof ParseException) {
                onError((ParseException) e, true);
            } else {
                onError(new ParseException("local", DataConfig.HTTP_NET_ERROR_MESSAGE)
                        , true);
            }
        } else {
            if (e instanceof ParseException) {
                onError((ParseException) e, false);
            } else {
                if (e instanceof HttpException) {
                    onError(new ParseException(String.valueOf(((HttpException) e).code()), DataConfig.HTTP_RESPONSE_ERROR_MESSAGE)
                            , false);
                } else {
                    onError(new ParseException("unknow", DataConfig.HTTP_RESPONSE_ERROR_MESSAGE)
                            , false);
                }
            }
        }
    }

    /**
     * 请求成功，取消请求
     */
    @Override
    public void onComplete() {

    }

    public void onError(@NonNull ParseException e, boolean isLocalError) {
    }

    public abstract void onSuccess(T t);
}
