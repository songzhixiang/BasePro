package www.andysong.com.basepro.example;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import butterknife.BindView;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.base.BaseFragment;
import www.andysong.com.basepro.utils.PermissionHelper;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class MainFragment extends BaseFragment {
    
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.bottomBar)
    SpaceNavigationView bottomBar;


    public static MainFragment newInstance() {

        Bundle args = new Bundle();
        
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ex_main;
    }

    @Override
    protected void initEventAndData(View mView) {


    }



}
