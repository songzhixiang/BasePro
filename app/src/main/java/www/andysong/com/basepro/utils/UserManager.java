package www.andysong.com.basepro.utils;

import www.andysong.com.basepro.db.RealmManager;
import www.andysong.com.basepro.modular.my.bean.User;
import www.andysong.com.basepro.modular.my.bean.UserBean;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/31
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class UserManager {

    private static User user = null;

    public static boolean isLogin() {
        return getUser() != null;
    }

    public static User getUser() {
        if (UserManager.user != null) {
            return UserManager.user;
        }

        return  RealmManager.INSTANCE.queryUserInfo();
    }

    public static void setUser(User user){
        UserManager.user = user;
        RealmManager.INSTANCE.insertUserInfo(user);
    }

    public static void logoutUser(){
        RealmManager.INSTANCE.deleteUserInfo();
        RealmManager.INSTANCE.deleteUserToken();
        UserManager.user = null;
    }
}
