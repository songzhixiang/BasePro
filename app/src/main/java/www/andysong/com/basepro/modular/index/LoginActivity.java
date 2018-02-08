package www.andysong.com.basepro.modular.index;

import android.os.Bundle;

import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.BaseActivity;
import www.andysong.com.basepro.example.ExLoginFragment;

/**
 * 登陆界面
 * Created by andysong on 2018/1/16.
 */

public class LoginActivity extends BaseActivity {
    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        if (findFragment(ExLoginFragment.class) == null) {
            loadRootFragment(R.id.fl_container, ExLoginFragment.newInstance());
        }
    }
}
