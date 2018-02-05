package www.andysong.com.basepro.example;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;

import butterknife.BindView;
import butterknife.OnClick;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.base.BaseFragment;
import www.andysong.com.basepro.http.HttpClientApi;
import www.andysong.com.basepro.http.ProgressHttpObserver;
import www.andysong.com.basepro.http.parser.ParseException;
import www.andysong.com.basepro.modular.my.bean.UserBean;
import www.andysong.com.basepro.utils.UserManager;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/18
 *     desc   : 示例登陆
 *     version: 1.0
 * </pre>
 */

public class ExLoginFragment extends BaseFragment {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.ll_edit_phone)
    LinearLayout llEditPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.ll_edit_pwd)
    LinearLayout llEditPwd;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.tv_login_tips)
    TextView tvLoginTips;
    @BindView(R.id.iv_qq_login)
    ImageView ivQqLogin;
    @BindView(R.id.iv_wechat_login)
    ImageView ivWechatLogin;
    @BindView(R.id.iv_alipay_login)
    ImageView ivAlipayLogin;

    public static ExLoginFragment newInstance() {

        Bundle args = new Bundle();

        ExLoginFragment fragment = new ExLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initEventAndData(View mView) {
        super.initEventAndData(mView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_aomi;
    }


    @OnClick({R.id.et_phone, R.id.et_pwd, R.id.button, R.id.iv_qq_login, R.id.iv_wechat_login, R.id.iv_alipay_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_phone:

                break;
            case R.id.et_pwd:

                break;
            case R.id.button:
                requestLogin();
                break;
            case R.id.iv_qq_login:
                break;
            case R.id.iv_wechat_login:
                break;
            case R.id.iv_alipay_login:
                break;
        }
    }

    private void requestLogin() {
        ArrayMap params = new ArrayMap();

        String mobile = etPhone.getText().toString();
        String password = etPwd.getText().toString();

        params.put("mobile",mobile);
        params.put("password",password);

        ArrayMap headParams = new ArrayMap();
        headParams.put("device-id", DeviceUtils.getAndroidID());
        headParams.put("device-id", DeviceUtils.getAndroidID());//设备id
        headParams.put("device-model", DeviceUtils.getManufacturer());//厂商
        headParams.put("device-token", "szx123456789");
        headParams.put("os-name", "Android");
        headParams.put("os-version", DeviceUtils.getSDKVersion());

        HttpClientApi.postHeader("api/v1/user/login", headParams, params, UserBean.class, false, new ProgressHttpObserver<UserBean>(this) {
            @Override
            public void onSuccess(UserBean userBean) {
                UserManager.setUser(userBean.getUser());
                LogUtils.e("登陆成功了");
            }

            @Override
            public void onError(ParseException e, boolean isLocalError) {
                super.onError(e, isLocalError);
            }
        },getLifecycleTransformer());
    }
}
