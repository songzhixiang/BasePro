package www.andysong.com.basepro.modular.favorite.ui;

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

public class FavoriteFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favorite;
    }

    public static FavoriteFragment newInstance() {

        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initEventAndData(View mView) {
        super.initEventAndData(mView);
        LogUtils.e("FavoriteFragment  加载了");
        mTopBar.setTitle("搜索");
        mTopBar.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.columbia_blue));
    }
}
