package www.andysong.com.basepro.modular.my.bean;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/31
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class User extends RealmObject implements Serializable {
    @PrimaryKey
    private String mobile;
    private boolean is_real_name_auth;
    private boolean is_bank_card_bind;
    private int is_consumer;
    private int is_trader;
    private int disabled;
    private double balance;
    private String frozen;
    private double bounty;
    private double flow_gold_amount;
    private double product_gold_amount;
    private double flow_money_last_income;
    private double flow_gold_last_income;
    private double flow_money_total_income;
    private double flow_gold_total_income;
    private double product_money_last_income;
    private double product_money_total_income;
    private double product_gold_total_income;
    private double product_gold_last_income;
    private boolean has_trader_pwd;
    private String real_name;
    private String id_card_no;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean getIs_real_name_auth() {
        return is_real_name_auth;
    }

    public void setIs_real_name_auth(boolean is_real_name_auth) {
        this.is_real_name_auth = is_real_name_auth;
    }

    public boolean getIs_bank_card_bind() {
        return is_bank_card_bind;
    }

    public void setIs_bank_card_bind(boolean is_bank_card_bind) {
        this.is_bank_card_bind = is_bank_card_bind;
    }

    public int getIs_consumer() {
        return is_consumer;
    }

    public void setIs_consumer(int is_consumer) {
        this.is_consumer = is_consumer;
    }

    public int getIs_trader() {
        return is_trader;
    }

    public void setIs_trader(int is_trader) {
        this.is_trader = is_trader;
    }

    public int getDisabled() {
        return disabled;
    }

    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public Object getBounty() {
        return bounty;
    }

    public void setBounty(double bounty) {
        this.bounty = bounty;
    }

    public double getFlow_gold_amount() {
        return flow_gold_amount;
    }

    public void setFlow_gold_amount(double flow_gold_amount) {
        this.flow_gold_amount = flow_gold_amount;
    }

    public double getProduct_gold_amount() {
        return product_gold_amount;
    }

    public void setProduct_gold_amount(double product_gold_amount) {
        this.product_gold_amount = product_gold_amount;
    }

    public double getFlow_money_last_income() {
        return flow_money_last_income;
    }

    public void setFlow_money_last_income(double flow_money_last_income) {
        this.flow_money_last_income = flow_money_last_income;
    }

    public double getFlow_gold_last_income() {
        return flow_gold_last_income;
    }

    public void setFlow_gold_last_income(double flow_gold_last_income) {
        this.flow_gold_last_income = flow_gold_last_income;
    }

    public double getFlow_money_total_income() {
        return flow_money_total_income;
    }

    public void setFlow_money_total_income(double flow_money_total_income) {
        this.flow_money_total_income = flow_money_total_income;
    }

    public double getFlow_gold_total_income() {
        return flow_gold_total_income;
    }

    public void setFlow_gold_total_income(double flow_gold_total_income) {
        this.flow_gold_total_income = flow_gold_total_income;
    }

    public double getProduct_money_last_income() {
        return product_money_last_income;
    }

    public void setProduct_money_last_income(double product_money_last_income) {
        this.product_money_last_income = product_money_last_income;
    }

    public double getProduct_money_total_income() {
        return product_money_total_income;
    }

    public void setProduct_money_total_income(double product_money_total_income) {
        this.product_money_total_income = product_money_total_income;
    }

    public double getProduct_gold_total_income() {
        return product_gold_total_income;
    }

    public void setProduct_gold_total_income(double product_gold_total_income) {
        this.product_gold_total_income = product_gold_total_income;
    }

    public double getProduct_gold_last_income() {
        return product_gold_last_income;
    }

    public void setProduct_gold_last_income(double product_gold_last_income) {
        this.product_gold_last_income = product_gold_last_income;
    }

    public boolean isHas_trader_pwd() {
        return has_trader_pwd;
    }

    public void setHas_trader_pwd(boolean has_trader_pwd) {
        this.has_trader_pwd = has_trader_pwd;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getId_card_no() {
        return id_card_no;
    }

    public void setId_card_no(String id_card_no) {
        this.id_card_no = id_card_no;
    }


}
