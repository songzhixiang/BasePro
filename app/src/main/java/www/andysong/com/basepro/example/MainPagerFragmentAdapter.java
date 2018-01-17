package www.andysong.com.basepro.example;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import www.andysong.com.basepro.base.BaseFragment;

/**
 * 主页Adapter
 * Created by andysong on 2018/1/16.
 */

public class MainPagerFragmentAdapter extends FragmentPagerAdapter {

    private BaseFragment[] mFragments = new BaseFragment[5];

    public MainPagerFragmentAdapter(FragmentManager fm,BaseFragment[] mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:


            case 1:


            case 2:


            case 3:


            case 4:


        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
