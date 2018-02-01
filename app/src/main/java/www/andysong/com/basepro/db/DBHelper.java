package www.andysong.com.basepro.db;

import www.andysong.com.basepro.modular.my.bean.Token;
import www.andysong.com.basepro.modular.my.bean.User;
import www.andysong.com.basepro.modular.my.bean.UserBean;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/31
 *     desc   : 数据库
 *     version: 1.0
 * </pre>
 */

public interface DBHelper {

    void insertUserInfo(User user);

    void insertUserToken(Token token);

    User queryUserInfo();

    Token queryUserToken();

    void deleteUserInfo();

    void deleteUserToken();
}
