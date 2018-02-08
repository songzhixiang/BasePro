package www.andysong.com.basepro;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;

import butterknife.BindView;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;
import www.andysong.com.basepro.core.base.BaseActivity;
import www.andysong.com.basepro.modular.index.LoginActivity;
import www.andysong.com.basepro.utils.UserManager;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/07
 *     desc   : 引导页
 *     version: 1.0
 * </pre>
 */

public class GuideActivity extends BaseActivity {

    private boolean isFromSplash;
    @BindView(R.id.banner_guide_background)
    BGABanner mBackgroundBanner;
    @BindView(R.id.banner_guide_foreground)
    BGABanner mForegroundBanner;

    @Override
    protected int getLayout() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {
        isFromSplash = getIntent().getBooleanExtra("isFromSplash", false);
        // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        // 设置数据源
        mBackgroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.uoko_guide_background_1,
                R.drawable.uoko_guide_background_2,
                R.drawable.uoko_guide_background_3);

        mForegroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.uoko_guide_foreground_1,
                R.drawable.uoko_guide_foreground_2,
                R.drawable.uoko_guide_foreground_3);

        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                close();

            }
        });
    }

    private void close() {
        if (isFromSplash) {
            if (GlobalConfig.mustLogin) {
                if (UserManager.isLogin()) {
                    ActivityUtils.startActivity(MainActivity.class);
                    ActivityCompat.finishAfterTransition(GuideActivity.this);
                } else {
                    ActivityUtils.startActivity(LoginActivity.class);
                    ActivityCompat.finishAfterTransition(GuideActivity.this);
                }
            } else {
                ActivityUtils.startActivity(MainActivity.class);
                ActivityCompat.finishAfterTransition(GuideActivity.this);
            }
        } else {
            ActivityCompat.finishAfterTransition(GuideActivity.this);
        }
    }

    @Override
    public void onBackPressedSupport() {

        close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        mBackgroundBanner.setBackgroundResource(android.R.color.white);
    }


}
