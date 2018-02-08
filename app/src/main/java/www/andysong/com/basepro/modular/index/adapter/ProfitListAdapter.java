package www.andysong.com.basepro.modular.index.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

import www.andysong.com.basepro.R;
import www.andysong.com.basepro.modular.index.bean.ProfitDetailsBean;

/**
 * Created by andysong on 2017/12/21.
 */

public class ProfitListAdapter extends BaseQuickAdapter<ProfitDetailsBean,BaseViewHolder> {
    public ProfitListAdapter(@Nullable List<ProfitDetailsBean> data) {
        super(R.layout.item_profit_details,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProfitDetailsBean item) {
        helper.setText(R.id.tv_profit_name,item.getName());
        helper.setText(R.id.tv_profit_time,item.getTime());
        switch (item.getType())
        {
            case -4://金生金购买（流动金购买）
                helper.setText(R.id.tv_profit,"+"+item.getGold_amount()+"g");
                helper.setText(R.id.tv_balance,"账户流动金：-"+item.getGold_amount()+"g");
                break;
            case -3://金生金购买（余额购买）
                helper.setText(R.id.tv_profit,"+"+item.getGold_amount()+"g");
                helper.setText(R.id.tv_balance,"余额：-"+item.getMoney_amount()+"元");
                break;
            case -2://流动金购买（余额购买）
                helper.setText(R.id.tv_profit,"+"+item.getGold_amount()+"g");
                helper.setText(R.id.tv_balance,"余额：-"+item.getMoney_amount()+"元");
                break;
            case -1://提现
                helper.setText(R.id.tv_profit,item.getMoney_amount()+"元");
                helper.setText(R.id.tv_balance,"余额：-"+item.getMoney_amount()+"元");
                break;
            case 1://充值
                helper.setText(R.id.tv_profit,"+"+item.getMoney_amount()+"元");
                helper.setText(R.id.tv_balance,"余额：+"+item.getMoney_amount()+"元");
                break;
            case 2://流动金赎回
                helper.setText(R.id.tv_profit,"-"+item.getGold_amount()+"g");
                helper.setText(R.id.tv_balance,"余额：+"+item.getMoney_amount()+"元");
                break;
            case 3://金生金赎回
                helper.setText(R.id.tv_profit,"-"+item.getGold_amount()+"g");
                helper.setText(R.id.tv_balance,"余额：+"+item.getMoney_amount()+"元");
                break;
            case 4://流动金收益
                helper.setText(R.id.tv_profit,"+"+item.getMoney_amount()+"元");
                helper.setText(R.id.tv_balance,"余额：+"+item.getMoney_amount()+"元");
                break;
            case 5://金生金收益
                helper.setText(R.id.tv_profit,"+"+item.getMoney_amount()+"元");
                helper.setText(R.id.tv_balance,"余额：+"+item.getMoney_amount()+"元");
                break;
        }
    }
}
