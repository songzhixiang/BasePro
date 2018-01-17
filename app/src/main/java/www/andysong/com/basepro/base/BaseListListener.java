package www.andysong.com.basepro.base;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by andysong on 2018/1/16.
 */

public interface BaseListListener {

    BaseQuickAdapter getAdapter();

    void loadData();

    void clearAndRefresh();

    void onSetAdapter();

    void setViewAnimatorPage(int index);

    View getErrorView();

    View getEmptyView();

    View getLoadingView();
}
