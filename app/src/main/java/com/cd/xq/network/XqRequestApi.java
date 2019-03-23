package com.cd.xq.network;

import com.cd.xq.beans.BGetArrays;
import com.cd.xq.beans.BGetConsumeHistory;
import com.cd.xq.beans.BGetGiftItem;
import com.cd.xq.beans.BGetPayHistory;
import com.cd.xq.beans.BGetPayItem;
import com.cd.xq.beans.BMakePayOrder;
import com.cd.xq.module.chart.beans.BGetReportItem;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.user.UserInfoBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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

    @GET("Pay/getPayItem")
    Observable<NetResult<List<BGetPayItem>>> getPayItem();

    @GET("Pay/getPayHistory")
    Observable<NetResult<List<BGetPayHistory>>> getPayHistory(@Query("userName") String userName
            ,@Query("status") int status);

    @GET("Pay/getGiftItem")
    Observable<NetResult<List<BGetGiftItem>>> getGiftItem();

    @GET("Pay/buyGiftByCoin")
    Observable<NetResult> buyGiftByCoin(@QueryMap Map<String, Object> map);

    @GET("Pay/getGiftList")
    Observable<NetResult<List<BGetGiftItem>>> getGiftList(@Query("userName") String userName);

    @GET("Pay/getConsumeHistory")
    Observable<NetResult<List<BGetConsumeHistory>>> getConsumeHistory(@Query("userName") String userName);

    @GET("Pay/makePayOrder")
    Observable<NetResult<BMakePayOrder>> makePayOrder(@QueryMap Map<String, Object> map);

    /**
     * 模拟
     * @param orderId
     * @return
     */
    @GET("Pay/handlePayCallback")
    Observable<NetResult> handlePayCallback(@Query("orderId") String orderId);
}
