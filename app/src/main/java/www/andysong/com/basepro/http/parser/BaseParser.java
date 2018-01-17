package www.andysong.com.basepro.http.parser;

import org.json.JSONException;
import org.json.JSONObject;


import www.andysong.com.basepro.http.DataConfig;

public abstract class BaseParser implements IParser {


    public static final String SUCCESS_CODE = "200";

    private static HttpLogoutListener mHttpLogoutListener;

    public static void setHttpLogoutListener(HttpLogoutListener mHttpLogoutListener) {
        BaseParser.mHttpLogoutListener = mHttpLogoutListener;
    }

    public abstract Object parseIType(JSONObject json) throws JSONException;

    @Override
    public Object parse(JSONObject jsonObject) throws ParseException {
        String code = jsonObject.optString("code");
        String msg = jsonObject.optString("msg");
        if (SUCCESS_CODE.equals(code)) {
            try {
                return parseIType(jsonObject);
            } catch (JSONException e) {
                throw new ParseException(code, DataConfig.HTTP_PARSE_ERROR_MESSAGE);
            }
            //token失效
        } else if ("401".equals(code)) {
            if (mHttpLogoutListener != null) {
                mHttpLogoutListener.onLogout();
            }
            throw new ParseException(code, msg);
        } else {
            throw new ParseException(code, msg);
        }
    }
}

