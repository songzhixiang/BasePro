package www.andysong.com.basepro.http;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * 接口访问Api
 * Created by andysong on 2018/1/16.
 */

public interface ServerApi {
    String HOST = "http://g-api.cdabon.com/";
    @POST
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<String> postString(@Url String url, @Body RequestBody body);

    @POST
    @FormUrlEncoded
    Observable<String> postFormString(@Url String url, @FieldMap Map<String, String> map, @HeaderMap Map<String, String> header);

    @GET
    Observable<String> getString(@Url String url, @QueryMap Map<String, String> map);

    @POST
    Observable<String> uploadFile(@Url String url, @Body RequestBody body);
}
