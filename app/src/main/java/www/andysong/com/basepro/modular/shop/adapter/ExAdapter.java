package www.andysong.com.basepro.modular.shop.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import www.andysong.com.basepro.R;
import www.andysong.com.basepro.modular.shop.bean.ExBean;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ExAdapter extends BaseQuickAdapter<ExBean,BaseViewHolder> {
    public ExAdapter(@Nullable List<ExBean> data) {
        super(R.layout.item_exbean,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExBean item) {
        Glide.with(mContext).load(item.getLogo()).into((ImageView) helper.getView(R.id.iv_guess_img));
        helper.setText(R.id.tv_guess_name,item.getSupplier_name());
        helper.setText(R.id.tv_start_count,item.getScore()+"分");
        helper.setText(R.id.tv_price,item.getAverage_spend()+"元/人");
        helper.setText(R.id.tv_guess_distance,item.getDistance()+"Km");
        helper.setText(R.id.tv_guess_type,item.getCategory());
        helper.setText(R.id.tv_guess_location,item.getAddress());
        if (!"0".equals(item.getFavour_discount()))
        {
            helper.getView(R.id.ll_discounts).setVisibility(View.VISIBLE);
            String str = "优惠宝消费"+item.getFavour_discount()+"折";
            int bsStart = str.indexOf(item.getFavour_discount());
            int bend = bsStart+item.getFavour_discount().length();
            SpannableStringBuilder style=new SpannableStringBuilder(str);
            style.setSpan(new ForegroundColorSpan(Color.RED),bsStart,bend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            helper.setText(R.id.tv_favour_discount,style);
        }else {
            helper.getView(R.id.ll_discounts).setVisibility(View.GONE);
        }


        String strRate = "赠送消费金额"+item.getFavour_rate()+"%优惠宝";
        int bsStart = strRate.indexOf(item.getFavour_rate()+"%");
        int bend = bsStart+(item.getFavour_rate()+"%").length();
        SpannableStringBuilder style=new SpannableStringBuilder(strRate);
        style.setSpan(new ForegroundColorSpan(Color.RED),bsStart,bend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        helper.setText(R.id.tv_favour_rate,style);

        String strRate_total = "已赠送"+item.getFavour_rate_total()+"优惠宝";
        int zsStart = strRate_total.indexOf(item.getFavour_rate_total());
        int zsBend = zsStart+item.getFavour_rate_total().length();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(strRate_total);
        stringBuilder.setSpan(new ForegroundColorSpan(Color.RED),zsStart,zsBend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        helper.setText(R.id.tv_favour_rate_total,stringBuilder);


        switch (item.getScore())
        {
            case "5":
                ((RatingBar)helper.getView(R.id.ratingbar)).setRating(5);
                break;
            case "4":
                ((RatingBar)helper.getView(R.id.ratingbar)).setRating(4);
                break;
            case "3":
                ((RatingBar)helper.getView(R.id.ratingbar)).setRating(3);
                break;
            case "2":
                ((RatingBar)helper.getView(R.id.ratingbar)).setRating(2);
                break;
            case "1":
                ((RatingBar)helper.getView(R.id.ratingbar)).setRating(1);
                break;
            default:
                ((RatingBar)helper.getView(R.id.ratingbar)).setRating(0);
        }
    }
}
