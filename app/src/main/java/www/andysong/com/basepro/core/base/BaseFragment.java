package www.andysong.com.basepro.core.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragmentDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * BaseFragment
 * Created by andysong on 2018/1/16.
 */

public abstract class BaseFragment extends Fragment{



    private Unbinder mUnBinder;
    protected View mView;
    protected BaseActivity mActivity;
    protected boolean isInited = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), null);
        return mView;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        mUnBinder.unbind();
    }


    protected abstract int getLayoutId();

    protected abstract void initEventAndData(View mView);


}
