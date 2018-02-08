package www.andysong.com.basepro.modular.index.bean;

import java.io.Serializable;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/02/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ProfitDetailsBean implements Serializable {

    private int id;
    private double money_amount;
    private double gold_amount;
    private int type;
    private String desc;
    private String time;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMoney_amount() {
        return money_amount;
    }

    public void setMoney_amount(double money_amount) {
        this.money_amount = money_amount;
    }

    public double getGold_amount() {
        return gold_amount;
    }

    public void setGold_amount(double gold_amount) {
        this.gold_amount = gold_amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

