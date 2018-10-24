package www.andysong.com.basepro.db;

import io.realm.Realm;
import io.realm.RealmConfiguration;


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



}
