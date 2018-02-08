package www.andysong.com.basepro.core.base;

import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.http.DefaultHttpObserver;
import www.andysong.com.basepro.http.HttpClientApi;
import www.andysong.com.basepro.http.parser.BaseParser;
import www.andysong.com.basepro.http.parser.ParseException;

/**
 * 基类RecyclerView的Fragment
 * Created by andysong on 2018/1/16.
 */

public abstract class BaseRecyclerFragment extends RootFragment implements BaseListListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    public RecyclerView mRecyclerView;
    protected BaseQuickAdapter mAdapter;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView.LayoutManager mLayoutManager;
    public int START_INDEX = 1;
    public int mPageSize = 20;//每页数据大小
    protected boolean isLoadedAllData = false;//是否加载完所有数据
    private boolean hasLoadMorePullLoadEnable = true;//是否可以上拉分页
    public int mCurrentIndex = START_INDEX;
    public int mOldIndex = START_INDEX;
    private ArrayList<View> mStateViews = new ArrayList<>();//状态View
    public Group mList = new Group();
    protected int mViewAnimatorIndex = 0;//默认显示loading
    public boolean isLoadedData = false;//是否已经加载过数据
    public Disposable mDisposable;

    @Override
    public void loadData() {
        isLoadedData = true;
        if (!mList.isEmpty()) {
            setViewAnimatorPage(3);
            onRefresh();
        } else {
            setViewAnimatorPage(0);
            onRefresh();
        }
    }

    public void initListView(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swiplayout);
        mRecyclerView =  view.findViewById(R.id.recylcerview);
        mLayoutManager = getRecyclerLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mList.clear();


        mAdapter = getAdapter();
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setNotDoAnimationCount(4);

        onSetAdapter();

        mSwipeRefreshLayout.setEnabled(true);
        mAdapter.setEnableLoadMore(false);
        mAdapter.setPreLoadNumber(mPageSize / 3);
        mRecyclerView.setAdapter(mAdapter);
        initViewAnimator();


    }

    /**
     * 布局方式
     * @return
     */
    public RecyclerView.LayoutManager getRecyclerLayoutManager() {
        return new LinearLayoutManager(_mActivity);
    }

    /**
     * 数据发生变化
     *
     * @param isLoaded 是否是加载数据
     */
    public void onDataChanged(boolean isLoaded) {
    }

    @Override
    public void clearAndRefresh() {
        mOldIndex = mCurrentIndex = START_INDEX;
        mList.clear();
        mAdapter.notifyDataSetChanged();
        isLoadedAllData = false;
        onDataChanged(false);
        setViewAnimatorPage(0);
        onRefresh();
    }


    /**
     * 自动刷新
     */
    public void autoSwipeRefresh() {
        if (mSwipeRefreshLayout != null) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                //控件内部bug导致必须先判断再调用
                mSwipeRefreshLayout.setRefreshing(true);
            }
            onRefresh();
        }
    }


    /**
     *
     * @param index 0,loading,1,error,2,empty，3，隐藏
     */
    @Override
    public void setViewAnimatorPage(int index) {
        mViewAnimatorIndex = index;
        if (index >= 3) {
            if (mAdapter.getEmptyView() != null) ((ViewGroup) mAdapter.getEmptyView()).removeAllViews();
        } else {
            mAdapter.setEmptyView(mStateViews.get(index));
        }
    }

    protected void initViewAnimator() {
        LayoutInflater inflater = _mActivity.getLayoutInflater();
        View loading = getLoadingView();
        if (loading != null) {
            mStateViews.add(loading);
        } else {
            mStateViews.add(inflater.inflate(R.layout.view_loading, null));
        }
        View error = getErrorView();
        if (error != null) {
            mStateViews.add(error);
        } else {
            error = inflater.inflate(R.layout.view_loading_error, null);
            mStateViews.add(error);
        }
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorRetry();
            }
        });
        View empty = getEmptyView();
        if (empty != null) {
            mStateViews.add(empty);
        } else {
            mStateViews.add(inflater.inflate(R.layout.view_loading_empty, null));
        }

    }

    public void onErrorRetry() {
        setViewAnimatorPage(0);
        onRefresh();
    }

    @Override
    public View getErrorView() {
        return null;
    }

    @Override
    public View getEmptyView() {
        return null;
    }

    @Override
    public View getLoadingView() {
        return null;
    }

    @Override
    public void getMessage() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = null;
        ArrayMap requestParams = new ArrayMap();
        final String url = getUrl();
        requestParams.put("page", mCurrentIndex);
        requestParams.put("pageSize", mPageSize);
        getRequestParams(requestParams);

        if (mCurrentIndex == START_INDEX) {
            mAdapter.setEnableLoadMore(false);
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            if (hasLoadMorePullLoadEnable) {
                mAdapter.setEnableLoadMore(true);
            } else {
                mAdapter.setEnableLoadMore(false);
            }
            mSwipeRefreshLayout.setEnabled(false);
        }
        HttpClientApi.get(url, requestParams, getBaseParser(), new DefaultHttpObserver(this) {
            @Override
            public void onStart(Disposable disposable) {
                super.onStart(disposable);
                mDisposable = disposable;
            }

            @Override
            public void onSuccess(Object o) {
                Group data;
                if (TextUtils.isEmpty(url)) {
                    data = new Group();
                    Class aClass = getMessageClass();
                    for (int i = 0; i < 10; i++) {
                        try {
                            data.add(aClass.newInstance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    data = (Group) o;
                }
                if (mCurrentIndex == START_INDEX) {
                    mList.clear();
//                    if (hasCache() && !TextUtils.isEmpty(url)) {
//                        CacheManager.getInstance().put(isCacheBindUser, getCacheKey(), JSON.toJSONString(data));
//                    }
                }
                mList.addAll(data);
                mAdapter.notifyDataSetChanged();
                onDataChanged(true);
                mSwipeRefreshLayout.setEnabled(true);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    //控件内部bug导致必须先判断再调用
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (hasLoadMorePullLoadEnable) {
                    mAdapter.setEnableLoadMore(true);
                    if (data.size() < mPageSize) {
                        //已加载完毕
                        mAdapter.loadMoreEnd();
                        isLoadedAllData = true;
                    } else {
                        mAdapter.loadMoreComplete();
                        isLoadedAllData = false;
                    }
                }
                onServerSuccess();
                if (mList.isEmpty()) {
                    setViewAnimatorPage(2);
                } else {
                    setViewAnimatorPage(3);
                }
            }

            @Override
            public void onError(@NonNull ParseException e, boolean isLocalError) {
                super.onError(e, isLocalError);
                mCurrentIndex = mOldIndex;
                if (mSwipeRefreshLayout.isRefreshing()) {
                    //控件内部bug导致必须先判断再调用
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (mViewAnimatorIndex == 2 || mViewAnimatorIndex == 3) {
                    //当正在显示内容页时，不跳转到错误页
                    mActivity.showErrorMsg(e.getMessage(),1000);
                } else {
                    setViewAnimatorPage(1);
                }
                if (hasLoadMorePullLoadEnable) {
                    mAdapter.loadMoreFail();
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        }, bindUntilEvent(FragmentEvent.DESTROY_VIEW));

    }




    @Override
    public void onRefresh() {
        mOldIndex = mCurrentIndex;
        mCurrentIndex = START_INDEX;
        getMessage();
    }

    @Override
    public void onLoadMoreRequested() {
        mOldIndex = mCurrentIndex;
        mCurrentIndex++;
        getMessage();
    }

    public void onServerSuccess() {
    }

    @Override
    public BaseParser getBaseParser() {
        final Class aClass = getMessageClass();
        return new BaseParser() {
            @Override
            public Object parseIType(JSONObject json) throws JSONException {
                Group list = new Group();
                if (json.getString("data").equals("null")) return list;
                List temp = JSON.parseArray(json.getString("data"), aClass);
                list.addAll(temp);
                return list;
            }
        };
    }

    public void setLoadMorePullLoadEnable(boolean loadMorePullLoadEnable) {
        this.hasLoadMorePullLoadEnable = loadMorePullLoadEnable;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mList.clear();
        mStateViews.clear();
        mList = null;
        mStateViews = null;
    }
}
