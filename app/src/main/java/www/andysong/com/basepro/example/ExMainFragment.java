package www.andysong.com.basepro.example;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.base.BaseFragment;
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
    protected void initEventAndData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

}
