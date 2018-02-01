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

public class Token extends RealmObject implements Serializable {
    /**
     * access : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vZy1hcGkuY2RhYm9uLmNvbS9hcGkvdjEvdXNlci9sb2dpbiIsImlhdCI6MTUxMzY3MzE1NSwiZXhwIjoxNTQ0Nzc3MTU1LCJuYmYiOjE1MTM2NzMxNTUsImp0aSI6ImVlcGxmMm9pMjlEamtJR3kiLCJzdWIiOjYsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.H4I3LrOHKlcJ-uKTI14tvROE3Io7hsY63Z-26GkuCpg
     * type : bearer
     * expires : 31104000
     */
    @PrimaryKey
    private String access;
    private String type;
    private int expires;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }
}
