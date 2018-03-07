package www.andysong.com.basepro.example;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;



import butterknife.BindView;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.SwipeBackActivity;

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

    @Override
    protected int getLayout() {
        return R.layout.activity_translucent;
    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState) {

    }
}
