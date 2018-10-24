package www.andysong.com.basepro.core.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;


/**
 * BaseActivity
 * Created by andysong on 2018/1/16.
 */

public abstract class BaseActivity extends AppCompatActivity{
    protected Activity mContext;
    private Unbinder mUnBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        if (getLayout()!=0)
        {
            setContentView(getLayout());
        }

        mUnBinder = ButterKnife.bind(this);
        initEventAndData(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }


    protected abstract int getLayout();
    protected abstract void initEventAndData(Bundle savedInstanceState);
}
