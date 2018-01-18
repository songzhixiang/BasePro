package www.andysong.com.basepro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

import www.andysong.com.basepro.utils.PermissionHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("onCreate");
        setContentView(R.layout.activity_main);
        initPermission();
    }

    private void initPermission() {
        PermissionHelper.requestStorage(null);
    }

}
