package www.andysong.com.basepro;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;

import www.andysong.com.basepro.core.base.BaseActivity;
import www.andysong.com.basepro.modular.index.LoginActivity;
import www.andysong.com.basepro.utils.UserManager;



/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/07
 *     desc   : 闪图
 *     version: 1.0
 * </pre>
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        if (GlobalConfig.hasGuide)
        {
            String GUIDE_KEY = "guide_";
            if (!SPUtils.getInstance().getBoolean(GUIDE_KEY,false))
            {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFromSplash",true);
                SPUtils.getInstance().put(GUIDE_KEY,true);
                ActivityUtils.startActivity(bundle,GuideActivity.class);
                ActivityCompat.finishAfterTransition(this);
                return;
            }
        }
        if (GlobalConfig.mustLogin)
        {
            if (UserManager.isLogin())
            {
                goHome();
            }else {
                ActivityUtils.startActivity(LoginActivity.class);
                ActivityCompat.finishAfterTransition(this);
            }
        }else {
            goHome();
        }
    }

    private void goHome() {
        ActivityUtils.startActivity(MainActivity.class);
        ActivityCompat.finishAfterTransition(this);
    }
}
