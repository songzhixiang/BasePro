package www.andysong.com.basepro.core.base;

import java.util.ArrayList;

/**
 * Created by andysong on 2018/1/16.
 */

public class Group<T> extends ArrayList<T> {
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
