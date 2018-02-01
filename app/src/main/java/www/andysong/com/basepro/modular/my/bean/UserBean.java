package www.andysong.com.basepro.modular.my.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/31
 *     desc   : 用户实体类
 *     version: 1.0
 * </pre>
 */

public class UserBean {

    /**
     * 转换为json字符串，用于本地存储
     *
     * @param user
     * @return
     */
    public static String toJsonString(User user) {
        return JSON.toJSONString(user);
    }

    /**
     * 将本地存储的用户转化为实体
     *
     * @param str
     * @return
     */
    public static User parseJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return JSON.parseObject(str, User.class);
    }

    private User user;
    private Token token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }


}
