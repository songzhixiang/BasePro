package www.andysong.com.basepro.http;

/**
 * 返回数据包装
 * Created by andysong on 2018/1/16.
 */

public class HttpResponse<T> {
    public static final String SUCCESS_CODE = "200";
    private String code;
    private String msg;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
