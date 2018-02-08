package www.andysong.com.basepro.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import www.andysong.com.basepro.core.base.BaseFragment;
import www.andysong.com.basepro.http.DefaultHttpObserver;
import www.andysong.com.basepro.http.HttpClientApi;
import www.andysong.com.basepro.http.parser.BaseParser;
import www.andysong.com.basepro.http.parser.ParseException;

/**
 * <pre>
 *     author : andysong
 *     e-mail : songzhixiang960425@gmail.com
 *     time   : 2018/01/26
 *     desc   : 上传图片
 *     version: 1.0
 * </pre>
 */

public class UpLoadImageHelper {


    public static void sendImage(final BaseFragment fragment, String imageUri, final String upurl, final boolean isCompress, final CallBack callBack) {
        final DefaultHttpObserver observer = new DefaultHttpObserver<List<String>>(fragment) {
            @Override
            public void onStart(final Disposable disposable) {
                super.onStart(disposable);
                fragment.showWaitingDialog(null, dialog -> {
                    if (disposable != null && !disposable.isDisposed()) {
                        disposable.dispose();
                    }
                    if (callBack != null) {
                        callBack.onCancel();
                    }
                });
            }

            @Override
            public void onSuccess(List<String> strings) {
                if (!strings.isEmpty()) {
                    if (callBack != null) {
                        callBack.onSuccess(strings.get(0));
                    }
                }
            }

            @Override
            public void onError(@NonNull ParseException e, boolean isLocalError) {
                super.onError(e, isLocalError);
                fragment.showErrorMsg("上传失败",1000);
                fragment.dismissWaitingDialog();
                if (callBack != null) {
                    callBack.onFailure();
                }
//                FileUtils.deleteTempFile(activity, imageUri);
            }

            @Override
            public void onComplete() {
                super.onComplete();
//                FileUtils.deleteTempFile(activity, imageUri);
            }
        };
        Observable observable = Observable.just(imageUri).flatMap((Function<String, ObservableSource<?>>) s -> {
            String imageUri1 = s;
            try {
                if (imageUri1 == null) {
                    return Observable.error(new RuntimeException(""));
                }
                String url = imageUri1;
                if (isCompress) {
//                    url = FileUtils.compressImage(activity, url, 1500, 1500, 400);
                    if (TextUtils.isEmpty(url)) {
                        return Observable.error(new RuntimeException(""));
                    }
                }
                File file = new File(url);
                String fileName = file.getName();
                String[] arry = fileName.split("\\.");
                String contentType = "image/jpeg";
                if (arry.length >= 2) {
                    if (arry[1].equalsIgnoreCase("jpg")
                            || arry[1].equalsIgnoreCase("jpeg")) {
                        contentType = "image/jpeg";
                    } else if (arry[1].equalsIgnoreCase("png")) {
                        contentType = "image/png";
                    }
                }
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("upload_file", fileName, RequestBody.create(MediaType.parse(contentType), file))
                        .build();

                return Observable.just(requestBody);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(new RuntimeException(""));
            }
        })
                .flatMap((Function) o -> HttpClientApi.getDefault().uploadFile(upurl, (RequestBody) o));
        HttpClientApi.doRequest(observable, new BaseParser() {
            @Override
            public Object parseIType(JSONObject json) throws JSONException {
                return JSON.parseArray(json.getString("data"), String.class);
            }
        }, observer, fragment.bindUntilEvent(FragmentEvent.DESTROY));
    }



    public interface CallBack {
        /**
         * 上传成功
         * @param s 服务器返回数据
         */
        void onSuccess(String s);

        /**
         * 上传失败
         */
        void onFailure();


        /**
         * 用户取消
         */
        void onCancel();
    }
}
