package com.cd.xq.module.util.network;

import com.cd.xq.module.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/4/28.
 */

public class NetWorkMg {
    //public static String IP_ADDRESS = Constant.CONSTANT_REMOTE_IP;
    public static String IP_ADDRESS = Constant.CONSTANT_LOCOL_IP;
    public static String GENDER = "男";

    public static Retrofit newRetrofit() {
        String BASEURL = "http://" + IP_ADDRESS + "/thinkphp/Sample_Mjmz/";
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20,TimeUnit.SECONDS)
                .build();

        Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        Retrofit retrofit=new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASEURL)
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
