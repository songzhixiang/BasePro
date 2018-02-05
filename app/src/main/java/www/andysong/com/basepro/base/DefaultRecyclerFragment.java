package www.andysong.com.basepro.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.http.NoDataResponse;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public abstract class DefaultRecyclerFragment extends BaseRecyclerFragment {
    private DisposableObserver mDisposableObserver;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void initEventAndData(View mView) {
        super.initEventAndData(mView);
        if (!isLoadedData) {
            if (mDisposableObserver != null && !mDisposableObserver.isDisposed()) {
                mDisposableObserver.dispose();
            }
            syncLoad();
        }
    }

    /**
     * 防止同时触发
     */
    private synchronized void syncLoad() {
        if (isLoadedData) return;
        loadData();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean isLoad = true;
        int loadDelay = 0;

//        if (isLoad) {
//            initListView(getView(), false);
//            mDisposableObserver = new DisposableObserver() {
//
//                @Override
//                public void onNext(Object o) {
//                    syncLoad();
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    syncLoad();
//                }
//
//                @Override
//                public void onComplete() {
//
//                }
//            };
//            Observable.create((ObservableOnSubscribe) emitter -> {
//                emitter.onNext(new NoDataResponse());
//                emitter.onComplete();
//            }).delay(loadDelay, TimeUnit.MILLISECONDS)
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(mDisposableObserver);
//        } else {
        initListView(getView(), isLoad);
//        }
    }

}
