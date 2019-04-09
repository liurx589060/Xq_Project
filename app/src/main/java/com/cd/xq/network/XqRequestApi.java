package com.cd.xq.network;

import com.cd.xq.beans.BGetArrays;
import com.cd.xq.beans.BGetConsumeHistory;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.beans.BGetPayHistory;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.user.UserInfoBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/12/9.
 */

public interface XqRequestApi {
    @GET("JMessage/getArrays")
    Observable<NetResult<List<BGetArrays>>> getArrays(@Query("public") int isPublic);

    @GET("JMessage/changePassword")
    Observable<NetResult<String>> changePassword(@Query("userName") String userName,@Query("password") String password);

    @GET("User/getFriendListByUser")
    Observable<NetResult<List<UserInfoBean>>> getFriendListByUser(@Query("userName") String userName);

    @GET("Pay/getPayHistory")
    Observable<NetResult<List<BGetPayHistory>>> getPayHistory(@Query("userName") String userName
            ,@Query("status") int status);

    @GET("Pay/getConsumeHistory")
    Observable<NetResult<List<BGetConsumeHistory>>> getConsumeHistory(@Query("userName") String userName);
}
