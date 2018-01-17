package www.andysong.com.basepro.http.parser;

/**
 * 解析接口异常
 * Created by andysong on 2018/1/17.
 */

public class ParseException extends Exception {
    public String SERVER_ERROR_CODE;

    public ParseException(String exceptionCode, String detailMessage) {
        super(detailMessage);
        SERVER_ERROR_CODE = exceptionCode;
    }
}
