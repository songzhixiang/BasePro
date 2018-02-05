package www.andysong.com.basepro.base;

import android.support.v4.util.ArrayMap;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import www.andysong.com.basepro.http.parser.BaseParser;

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

    void getMessage();

    String getUrl();

    void getRequestParams(ArrayMap params);

    Class getMessageClass();

    void onServerSuccess();

    BaseParser getBaseParser();

    void setLoadMorePullLoadEnable(boolean loadMorePullLoadEnable);
}
