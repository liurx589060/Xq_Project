package com.cd.xq.module.util.network;

import android.util.Base64;
import android.util.Log;

import com.cd.xq.module.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2018/4/28.
 */

public class NetWorkMg {
    //public static String IP_ADDRESS = Constant.CONSTANT_REMOTE_IP;
    public static String IP_ADDRESS = Constant.CONSTANT_LOCOL_IP;
    public static String GENDER = "男";

    public static Retrofit newRetrofit() {
        String BASEURL = "http://" + IP_ADDRESS + "/thinkphp/Sample_Mjmz/";
        return newRetrofit(BASEURL);
    }

    public static Retrofit newRetrofit(String baseUrl) {
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20,TimeUnit.SECONDS)
                .build();

        Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        Retrofit retrofit=new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return retrofit;
    }

    /**
     * JM Rest Api
     * @return
     */
    public static Retrofit newJMRestApiRetrofit() {
        String baseUrl = "https://api.im.jpush.cn/";
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20,TimeUnit.SECONDS);
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String value = Constant.JM_APP_KEY + ":" + Constant.JM_SECRET;
                String base64Value = Base64.encodeToString(value.getBytes(),Base64.DEFAULT);
                base64Value = base64Value.replaceAll("\n", "").replaceAll("\r", "");
                Request request = original.newBuilder()
                        .header("Authorization", "Basic " + base64Value)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });

        Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        Retrofit retrofit=new Retrofit.Builder()
                .client(okHttpClient.build())
                //.addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return retrofit;
    }

    public static String getCameraUrl() {
        return "rtmp://" + NetWorkMg.IP_ADDRESS + "/live/stream1";
    }

    public static String getAudioUrl_Guest() {
        return "rtmp://" + NetWorkMg.IP_ADDRESS + "/live/stream2";
    }

    public static String getAudioUrl_Angel() {
        return "rtmp://" + NetWorkMg.IP_ADDRESS + "/live/stream3";
    }
}
