package www.andysong.com.basepro.example;

import android.os.Bundle;

import www.andysong.com.basepro.R;
import www.andysong.com.basepro.base.BaseFragment;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/18
 *     desc   :示例登陆
 *     version: 1.0
 * </pre>
 */

public class ExLoginFragment extends BaseFragment {

    public static ExLoginFragment newInstance() {

        Bundle args = new Bundle();

        ExLoginFragment fragment = new ExLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initEventAndData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_aomi;
    }
}
