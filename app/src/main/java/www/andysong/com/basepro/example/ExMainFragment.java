package www.andysong.com.basepro.example;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import butterknife.BindView;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.BaseFragment;
import www.andysong.com.basepro.custom_view.BottomBar;

/**
 * 示例MainFragment+lazyload+bottombar+viewpager
 * Created by andysong on 2018/1/16.
 */

public class ExMainFragment extends BaseFragment {


    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.bottomBar)
    BottomBar mTab;

    public static ExMainFragment newInstance() {

        Bundle args = new Bundle();

        ExMainFragment fragment = new ExMainFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initEventAndData(View mView) {

    }

}
