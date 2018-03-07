package www.andysong.com.basepro.modular.shop.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;

import butterknife.BindView;
import butterknife.OnClick;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.BaseFragment;
import www.andysong.com.basepro.example.ExSwipeBackActivity;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ShopFragment extends BaseFragment {

    @BindView(R.id.btn_start_swipeactivity)
    Button btnStartSwipeactivity;
    @BindView(R.id.btn_start_swipefragment)
    Button btnStartSwipefragment;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    public static ShopFragment newInstance() {

        Bundle args = new Bundle();

        ShopFragment fragment = new ShopFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initEventAndData(View mView) {
        LogUtils.e("ShopFragment  加载了");
        setHeader(R.color.colorAccent,0, "ShopFragment", "点赞",this);

    }

    @OnClick({R.id.btn_start_swipeactivity, R.id.btn_start_swipefragment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_swipeactivity:
                ActivityUtils.startActivity(ExSwipeBackActivity.class);
                break;
            case R.id.btn_start_swipefragment:
                ActivityUtils.startActivity(SwipeBackActivityDemo.class);
                break;
        }
    }
}
