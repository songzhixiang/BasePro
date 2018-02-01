package www.andysong.com.basepro.example;

import android.os.Bundle;

import www.andysong.com.basepro.R;
import www.andysong.com.basepro.base.BaseActivity;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/18
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ExLoginActivity extends BaseActivity {

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
