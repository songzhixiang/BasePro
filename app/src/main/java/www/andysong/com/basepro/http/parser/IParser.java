package www.andysong.com.basepro.http.parser;

import org.json.JSONObject;


/**
 * Created by andysong on 2018/1/17.
 */

public interface IParser {
    Object parse(JSONObject jsonObject) throws ParseException;
}
