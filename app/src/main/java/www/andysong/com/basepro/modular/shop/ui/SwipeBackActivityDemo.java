package www.andysong.com.basepro.modular.shop.ui;

import android.os.Bundle;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.SwipeBackActivity;
import www.andysong.com.basepro.core.swipeback.SwipeBackLayout;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class SwipeBackActivityDemo extends SwipeBackActivity {
    @Override
    protected int getLayout() {
        return R.layout.activity_swipe_back;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        if (findFragment(SwipeBackFragmentDemo.class) == null) {
            loadRootFragment(R.id.fl_container, SwipeBackFragmentDemo.newInstance());
        }
        if (getSwipeBackLayout()!=null)
        {
            getSwipeBackLayout().setEdgeOrientation(SwipeBackLayout.EDGE_ALL);
        }
    }

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     *
     * @return true: Activity优先滑动退出;  false: Fragment优先滑动退出
     */
    @Override
    public boolean swipeBackPriority() {
        return super.swipeBackPriority();
    }

    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
}
