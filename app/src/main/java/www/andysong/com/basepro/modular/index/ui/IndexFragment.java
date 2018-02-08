package www.andysong.com.basepro.modular.index.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import www.andysong.com.basepro.R;
import www.andysong.com.basepro.core.base.DefaultRecyclerFragment;
import www.andysong.com.basepro.core.base.Group;
import www.andysong.com.basepro.http.parser.BaseParser;
import www.andysong.com.basepro.modular.index.adapter.ProfitListAdapter;
import www.andysong.com.basepro.modular.index.bean.ProfitDetailsBean;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class IndexFragment extends DefaultRecyclerFragment {


    @BindView(R.id.topbar)
    QMUITopBar topbar;



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
        topbar.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.columbia_blue));
        topbar.addRightImageButton(R.drawable.ic_camera,1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("点击了右边");
            }
        });
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new ProfitListAdapter(mList);
    }

    @Override
    public void onSetAdapter() {

    }

    @Override
    public String getUrl() {
        return "api/v1/user/trade";
    }

    @Override
    public void getRequestParams(ArrayMap params) {
        params.put("type",3);
    }

    @Override
    public Class getMessageClass() {
        return ProfitDetailsBean.class;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index;
    }

    public BaseParser getBaseParser() {
        final Class aClass = getMessageClass();
        return new BaseParser() {
            @Override
            public Object parseIType(JSONObject json) throws JSONException {
                Group list = new Group();
                if (json.getJSONObject("data").getString("result").equals("null")) return list;
                List temp = JSON.parseArray(json.getJSONObject("data").getString("result"), aClass);
                list.addAll(temp);
                return list;
            }
        };
    }
}
