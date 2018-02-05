package www.andysong.com.basepro.modular.shop.ui;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.chad.library.adapter.base.BaseQuickAdapter;

import www.andysong.com.basepro.base.DefaultRecyclerFragment;
import www.andysong.com.basepro.modular.shop.adapter.ExAdapter;
import www.andysong.com.basepro.modular.shop.bean.ExBean;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ExListFragment extends DefaultRecyclerFragment {

    public static ExListFragment newInstance() {

        Bundle args = new Bundle();
        
        ExListFragment fragment = new ExListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public BaseQuickAdapter getAdapter() {
        return new ExAdapter(null);
    }

    @Override
    public void onSetAdapter() {

    }

    @Override
    public String getUrl() {
        return "api/v1/suppliers/guess_you_like?history[]=80&city=成都";
    }

    @Override
    public void getRequestParams(ArrayMap params) {
        
    }

    @Override
    public Class getMessageClass() {
        return ExBean.class;
    }
}
