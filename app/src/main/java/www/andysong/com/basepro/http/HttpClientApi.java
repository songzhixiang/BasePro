package www.andysong.com.basepro.http;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import www.andysong.com.basepro.db.RealmManager;
import www.andysong.com.basepro.http.parser.BaseParser;
import www.andysong.com.basepro.http.parser.IParser;
import www.andysong.com.basepro.http.parser.ParseException;
import www.andysong.com.basepro.utils.UserManager;

/**
 *请求帮助类
 * Created by andysong on 2018/1/16.
 */

public class HttpClientApi {

    private static ServerApi SERVICE;


    public static ServerApi getDefault() {
        if (SERVICE == null) {
            //构建builder
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //设置超时时间
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS);
            builder.writeTimeout(20, TimeUnit.SECONDS);
            //错误重连
            builder.retryOnConnectionFailure(true);
            //设置统一的请求头部参数
            builder.addInterceptor(chain -> {
                Request original = chain.request();
                if (true) {
                    Request request = original.newBuilder()
                            .header("Authorization",
                            "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vZy1hcGkuY2RhYm9uLmNvbS9hcGkvdjEvdXNlci9sb2dpbiIsImlhdCI6MTUxODI0NTk4NiwiZXhwIjoxNTQ5MzQ5OTg2LCJuYmYiOjE1MTgyNDU5ODYsImp0aSI6IkxveDA3eENzY0dxV0xRaDciLCJzdWIiOjM2LCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3In0.blPoqV793sl8J8ATgemcS9G2MZtZd6snKEFngbGVqDs")
                            .header("device-id", DeviceUtils.getAndroidID())
                            .header("device-model", DeviceUtils.getManufacturer())
                            .header("os-name", "Android")
                            .header("os-version", DeviceUtils.getSDKVersionCode() + "")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                } else {
                    return chain.proceed(original);
                }
            });
            //缓存设置
            File cacheFile = new File(DataConfig.PATH_CACHE);
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
            Interceptor cacheInterceptor = chain -> {
                Request request = chain.request();
                if (!NetworkUtils.isAvailableByPing()) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                if (NetworkUtils.isAvailableByPing()) {
                    int maxAge = 0;
                    // 有网络时, 不缓存, 最大保存时长为0
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    // 无网络时，设置超时为4周
                    int maxStale = 60 * 60 * 24;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            };
            builder.addNetworkInterceptor(cacheInterceptor);
            builder.addInterceptor(cacheInterceptor);
            builder.cache(cache);
            //测试版本就打印请求Body
            if (DataConfig.isTest) {
                builder.addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY));
            }
            //构建请求
            SERVICE = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(ServerApi.HOST)
                    .build().create(ServerApi.class);
        }
        return SERVICE;
    }

    ///////////////////////////////////////////////////////////////////////////
    // get请求
    ///////////////////////////////////////////////////////////////////////////


    /**
     * get请求 返回格式data{xxxx}
     * @param url 请求url
     * @param params 请求参数
     * @param aclass 返回的Class对象
     * @param isList 是否返回的数组对象
     * @param observer observer
     * @param lifecycle 生命周期，与RxJava进行绑定，避免内存泄露
     */
    public static void get(String url, ArrayMap params,
                           final Class aclass, final boolean isList,
                           HttpObserver observer, LifecycleTransformer lifecycle) {
        BaseParser iParser = new BaseParser() {
            @Override
            public Object parseIType(JSONObject json) throws JSONException {
                if (isList) {
                    if (json.getString("data").equals("null")) return new ArrayList<>();
                    return JSON.parseArray(json.getString("data"), aclass);
                } else {
                    if (aclass.getSimpleName().equals(String.class.getSimpleName())) {
                        return json.optString("data");
                    }
                    return JSON.parseObject(json.getString("data"), aclass);
                }
            }
        };
        get(url, params, iParser, observer, lifecycle);
    }

    /**
     * get请求 返回格式data{result{xxxx}}
     * @param url
     * @param params
     * @param aclass
     * @param isList
     * @param observer
     * @param lifecycle
     */
    public static void getResult(String url, ArrayMap params,
                                 final Class aclass, final boolean isList,
                                 HttpObserver observer, LifecycleTransformer lifecycle) {
        BaseParser iParser = new BaseParser() {
            @Override
            public Object parseIType(JSONObject json) throws JSONException {
                if (isList) {
                    if (json.getJSONObject("data").getString("result").equals("null")) return new ArrayList<>();
                    return JSON.parseArray(json.getJSONObject("data").getString("result"), aclass);
                } else {
                    if (aclass.getSimpleName().equals(String.class.getSimpleName())) {
                        return json.getJSONObject("data").optString("result");
                    }
                    return JSON.parseObject(json.getJSONObject("data").getString("result"), aclass);
                }
            }
        };
        get(url, params, iParser, observer, lifecycle);
    }



    public static void get(String url, ArrayMap params, IParser parser,
                           final HttpObserver observer, LifecycleTransformer lifecycle) {
        if (TextUtils.isEmpty(url)) {
            //模拟
            observer.onNext(null);
            observer.onComplete();
            return;
        }
        if (parser == null) {
            parser = new BaseParser() {
                @Override
                public Object parseIType(JSONObject json) throws JSONException {
                    return null;
                }
            };
        }
        Observable ob = getDefault().getString(url, params);
        doRequest(ob, parser, observer, lifecycle);
    }

    ///////////////////////////////////////////////////////////////////////////
    // post请求
    ///////////////////////////////////////////////////////////////////////////

    public static void post(String url, ArrayMap params,
                            final Class aclass, final boolean isList,
                            HttpObserver observer, LifecycleTransformer lifecycle) {
        BaseParser iParser = new BaseParser() {
            @Override
            public Object parseIType(JSONObject json) throws JSONException {
                if (isList) {
                    if (json.getString("data").equals("null")) return new ArrayList<>();
                    return JSON.parseArray(json.getString("data"), aclass);
                } else {
                    if (aclass.getSimpleName().equals(String.class.getSimpleName())) {
                        return json.optString("data");
                    }
                    return JSON.parseObject(json.getString("data"), aclass);
                }
            }
        };
        post(url, params, iParser, observer, lifecycle);
    }


    public static void post(String url, ArrayMap params, IParser parser,
                            final HttpObserver observer, LifecycleTransformer lifecycle) {
        if (TextUtils.isEmpty(url)) {
            //模拟
            observer.onNext(null);
            observer.onComplete();
            return;
        }
        if (parser == null) {
            parser = new BaseParser() {
                @Override
                public Object parseIType(JSONObject json) throws JSONException {
                    return null;
                }
            };
        }
        Observable ob = getDefault().postFormString(url, params, new ArrayMap<>());
        doRequest(ob, parser, observer, lifecycle);
    }

    public static void postHeader(String url, ArrayMap header, ArrayMap params,
                                  final Class aclass, final boolean isList,
                                  final HttpObserver observer, LifecycleTransformer lifecycle) {
        BaseParser iParser;
        if (aclass == null) {
            iParser = new BaseParser() {
                @Override
                public Object parseIType(JSONObject json) throws JSONException {
                    return null;
                }
            };
        } else {
            iParser = new BaseParser() {
                @Override
                public Object parseIType(JSONObject json) throws JSONException {
                    if (isList) {
                        if (json.getString("data").equals("null")) return new ArrayList<>();
                        return JSON.parseArray(json.getString("data"), aclass);
                    } else {
                        if (aclass.getSimpleName().equals(String.class.getSimpleName())) {
                            return json.optString("data");
                        }
                        return JSON.parseObject(json.getString("data"), aclass);
                    }
                }
            };
        }
        Observable ob = getDefault().postFormString(url, params, header);
        doRequest(ob, iParser, observer, lifecycle);
    }


    /**
     * 请求
     * @param ob
     * @param parser
     * @param observer
     * @param lifecycle
     */

    public static void doRequest(Observable ob, IParser parser,
                                 HttpObserver observer, LifecycleTransformer lifecycle) {
        ObservableTransformer result = handleResult(parser);
        Observable observable = ob.compose(result);
        if (lifecycle != null) {
            observable = observable.compose(lifecycle);
        }

        if (observer == null) {
            observer = new HttpObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(@NonNull Object o) {

                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };
        }
        observable.subscribe(observer);
    }


    /**
     * 对结果进行预处理
     */
    public static ObservableTransformer handleResult(final IParser parser) {
        return upstream -> {
            Observable ret = upstream.flatMap(o -> {
                //使用parser模式
                if (o instanceof String) {
                    try {
                        Object result = parser.parse(new JSONObject((String) o));
                        return createData(result);
                    } catch (ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        return Observable.error(e);
                    } catch (JSONException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        return Observable.error(new ParseException("unknow", DataConfig.HTTP_PARSE_ERROR_MESSAGE));
                    }
                } else {
                    return Observable.error(new ParseException("unknow", DataConfig.HTTP_PARSE_ERROR_MESSAGE));
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            return ret;
        };
    }


    /**
     * 创建成功的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Observable createData(final T data) {
        return Observable.create((ObservableOnSubscribe) e -> {
            try {
                if (data == null) {
                    e.onNext(new NoDataResponse());
                } else {
                    e.onNext(data);
                }
                e.onComplete();
            } catch (Exception ex) {
                e.onError(new ParseException("local", DataConfig.HTTP_PARSE_ERROR_MESSAGE));
            }
        });
    }

    interface HttpObserver<T> extends Observer<T> {

    }

}
