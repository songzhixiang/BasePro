package www.andysong.com.basepro.modular.index.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.base.BaseFragment;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class IndexFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar topbar;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index;
    }

    public static IndexFragment newInstance() {

        Bundle args = new Bundle();

        IndexFragment fragment = new IndexFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initEventAndData(View mView) {
        super.initEventAndData(mView);
        LogUtils.e("IndexFragment  加载了");
        topbar.setTitle("首页");
        topbar.addRightImageButton(R.drawable.ic_camera,1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("点击了右边");
            }
        });
    }
}
