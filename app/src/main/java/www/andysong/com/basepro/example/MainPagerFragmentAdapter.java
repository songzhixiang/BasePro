package www.andysong.com.basepro.example;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import www.andysong.com.basepro.base.BaseFragment;
import www.andysong.com.basepro.modular.favorite.ui.FavoriteFragment;
import www.andysong.com.basepro.modular.index.ui.IndexFragment;
import www.andysong.com.basepro.modular.my.ui.MyFragment;
import www.andysong.com.basepro.modular.shop.ui.ShopFragment;

/**
 * 主页Adapter
 * Created by andysong on 2018/1/16.
 */

public class MainPagerFragmentAdapter extends FragmentPagerAdapter {

    private BaseFragment[] mFragments = new BaseFragment[4];

    public MainPagerFragmentAdapter(FragmentManager fm,BaseFragment[] mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return IndexFragment.newInstance();
            case 1:

                return ShopFragment.newInstance();
            case 2:

                return FavoriteFragment.newInstance();
            case 3:

                return MyFragment.newInstance();


        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
