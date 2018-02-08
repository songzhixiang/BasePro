package www.andysong.com.basepro.db;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import www.andysong.com.basepro.modular.my.bean.Token;
import www.andysong.com.basepro.modular.my.bean.User;

/**
 * Created by andysong on 2018/1/16.
 */

public enum RealmManager implements DBHelper {

    INSTANCE;

    private static final String DB_NAME = "myRealm.realm";

    private Realm mRealm = Realm.getInstance(new RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .name(DB_NAME)
            .build());


    @Override
    public void insertUserInfo(User user) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(user);
        mRealm.commitTransaction();
    }

    @Override
    public void insertUserToken(Token token) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(token);
        mRealm.commitTransaction();
    }

    @Override
    public User queryUserInfo() {
        return mRealm.where(User.class).findFirst();
    }

    @Override
    public Token queryUserToken() {
        return mRealm.where(Token.class).findFirst();
    }

    @Override
    public void deleteUserInfo() {
        User user = mRealm.where(User.class).findFirst();
        mRealm.beginTransaction();
        if (user != null) {
            user.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }

    @Override
    public void deleteUserToken() {
        Token token = mRealm.where(Token.class).findFirst();
        mRealm.beginTransaction();
        if (token != null) {
            token.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }
}
