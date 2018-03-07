package www.andysong.com.basepro.modular.my.ui;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.BaseFragment;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class MyFragment extends BaseFragment {



    public static MyFragment newInstance() {

        Bundle args = new Bundle();

        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initEventAndData(View mView) {
        LogUtils.e("MyFragment  加载了");
        setHeader(R.color.chartreuse,0, "MyFragment", "点赞",this);
    }
}
