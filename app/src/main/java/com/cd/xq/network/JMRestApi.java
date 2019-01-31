package com.cd.xq.network;

import com.cd.xq.module.util.beans.user.UserResp;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2019/1/28.
 */

public interface JMRestApi {
    @POST("v1/users/userstat")
    Observable<String> userStat(@Body RequestBody body);
}
