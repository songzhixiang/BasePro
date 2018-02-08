package www.andysong.com.basepro.modular.shop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.SwipeFragment;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class SwipeBackFragmentDemo extends SwipeFragment {

    @BindView(R.id.btn)
    Button btn;

    public static SwipeBackFragmentDemo newInstance() {

        Bundle args = new Bundle();

        SwipeBackFragmentDemo fragment = new SwipeBackFragmentDemo();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guide_three;
    }

    @Override
    protected void initEventAndData(View mView) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setParallaxOffset(0.5f);
    }


    @OnClick(R.id.btn)
    public void onViewClicked() {
        start(SwipeBackFragmentDemo.newInstance());
    }
}
