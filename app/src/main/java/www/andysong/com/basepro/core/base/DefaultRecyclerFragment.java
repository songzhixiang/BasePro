package www.andysong.com.basepro.core.base;

import android.os.Bundle;
import android.view.View;

import www.andysong.com.basepro.R;

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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler;
    }

    @Override
    protected void initEventAndData(View mView) {

        if (!isLoadedData) {

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
        initListView(getView());

    }


}
