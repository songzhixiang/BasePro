package www.andysong.com.basepro.modular.favorite.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

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
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        LogUtils.e("FavoriteFragment  加载了");
    }
}
