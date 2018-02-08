package www.andysong.com.basepro.example;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.SwipeBackActivity;
import www.andysong.com.basepro.modular.shop.ui.SwipeBackFragmentDemo;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ExSwipeBackActivity extends SwipeBackActivity {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @Override
    protected int getLayout() {
        return R.layout.activity_translucent;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        if (findFragment(SwipeBackFragmentDemo.class) == null) {
            loadRootFragment(R.id.fl_container, SwipeBackFragmentDemo.newInstance());
        }


//        getSwipeBackLayout().setEdgeOrientation(SwipeBackLayout.EDGE_ALL);
        QMUIStatusBarHelper.translucent(this);
        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mTopBar.setTitle("沉浸式状态栏示例");
    }
}
