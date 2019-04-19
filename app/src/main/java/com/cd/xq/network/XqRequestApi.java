package com.cd.xq.network;

import com.cd.xq.beans.BCheckRoomExpiry;
import com.cd.xq.beans.BCheckUpdate;
import com.cd.xq.beans.BGetArrays;
import com.cd.xq.beans.BGetBanner;
import com.cd.xq.beans.BGetChatRoomList;
import com.cd.xq.beans.BGetConsumeHistory;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.beans.BGetPayHistory;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.BChatRoom;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
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
    @GET("JMessage/getChatRoomList")
    Observable<NetResult<List<BChatRoom>>> getChatRoomList(@QueryMap Map<String, Object> map);

    @GET("JMessage/changePassword")
    Observable<NetResult<String>> changePassword(@Query("userName") String userName,@Query("password") String password);

    @GET("User/getFriendListByUser")
    Observable<NetResult<List<UserInfoBean>>> getFriendListByUser(@Query("userName") String userName);

    @GET("Pay/getPayHistory")
    Observable<NetResult<List<BGetPayHistory>>> getPayHistory(@Query("userName") String userName
            ,@Query("status") int status);

    @GET("Pay/getConsumeHistory")
    Observable<NetResult<List<BGetConsumeHistory>>> getConsumeHistory(@Query("userName") String userName);

    @GET("Pay/checkRoomExpiry")
    Observable<NetResult<BCheckRoomExpiry>> checkRoomExpiry(@Query("userName") String userName
            , @Query("handleType") int handleType);

    @GET("JMessage/getChatRoomListByUser")
    Observable<NetResult<List<BGetChatRoomList>>> getChatRoomListByUser(@Query("userName") String userName);

    @GET("Api/checkUpdate")
    Observable<NetResult<BCheckUpdate>> checkUpdate(@Query("versionCode") int versionCode);

    @GET("Api/getBanner")
    Observable<NetResult<List<BGetBanner>>> getBanner();
}
