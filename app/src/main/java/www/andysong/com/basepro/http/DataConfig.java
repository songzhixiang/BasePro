package www.andysong.com.basepro.http;

import java.io.File;

import www.andysong.com.basepro.app.MyApp;

/**
 * Created by andysong on 2018/1/16.
 */

public class DataConfig {

    /**
     * 是否是测试阶段,请到GlobalConfig修改配置
     */
    public static boolean isTest = true;


    public static final String PATH_DATA = MyApp.getContext().getCacheDir().getAbsolutePath() + File.separator + "data";

    public static final String PATH_CACHE = PATH_DATA + "/NetCache";

    /**
     * 本地网络错误
     */
    public static final String HTTP_NET_ERROR_MESSAGE = "网络无法连接，请稍后重试";
    /**
     * 解析错误
     */
    public static final String HTTP_PARSE_ERROR_MESSAGE = "服务器数据有误，请稍后重试";
    /**
     * 服务器应答httpcode错误
     */
    public static final String HTTP_RESPONSE_ERROR_MESSAGE = "网络无法连接，请稍后重试";

}
