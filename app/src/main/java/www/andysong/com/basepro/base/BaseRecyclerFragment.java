package www.andysong.com.basepro.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

import www.andysong.com.basepro.R;

/**
 * 基类RecyclerView的Fragment
 * Created by andysong on 2018/1/16.
 */

public class BaseRecyclerFragment extends BaseFragment implements BaseListListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

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
    @Override
    protected void initEventAndData() {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return null;
    }

    @Override
    public void loadData() {
        isLoadedData = true;
    }

    public void initListView(View view, boolean isLoadData) {
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
        if (isLoadData)
        {
            loadData();
        }

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

    @Override
    public void onSetAdapter() {

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
            mStateViews.add(inflater.inflate(R.layout.view_loading_error, null));
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
    public void onRefresh() {
        mOldIndex = mCurrentIndex;
        mCurrentIndex = START_INDEX;
    }

    @Override
    public void onLoadMoreRequested() {
        mOldIndex = mCurrentIndex;
        mCurrentIndex++;
    }
}
