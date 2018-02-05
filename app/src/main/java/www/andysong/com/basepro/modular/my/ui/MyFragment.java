package www.andysong.com.basepro.modular.my.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.base.BaseFragment;

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

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;

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
        super.initEventAndData(mView);
        LogUtils.e("MyFragment  加载了");
        mTopBar.setTitle("搜索");
        mTopBar.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.cornflower));
    }
}
