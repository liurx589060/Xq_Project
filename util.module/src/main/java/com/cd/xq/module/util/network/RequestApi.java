package com.cd.xq.module.util.network;

import com.cd.xq.module.util.beans.BaseResp;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.BChatRoom;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.beans.user.BBlackUser;
import com.cd.xq.module.util.beans.user.UserResp;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2018/4/28.
 */

public interface RequestApi {
    @GET("User/regist")
    Observable<UserResp> regist(@Query("userName") String userName, @Query("password") String password);

    @GET("User/login")
    Observable<UserResp> login(@Query("userName") String userName, @Query("password") String password);

    @GET("User/updateUserInfo")
    Observable<UserResp> updateUserInfo(@QueryMap Map<String, Object> map);

    @GET("User/getUserInfoByUserName")
    Observable<UserResp> getUserInfoByUserName(@Query("userName") String userName);

    @GET("User/checkUserExist")
    Observable<BaseResp> checkUserExist(@Query("userName") String userName);

    @GET("JMessage/getChartRoomMemeberList")
    Observable<JMChartResp> getChartRoomMemeberList(@Query("roomId") long roomId);

    @GET("JMessage/getChartRoomMemeberList")
    Observable<JMChartResp> getChartMembersByUserName(@Query("userName") String userName);

    @GET("JMessage/appointChatRoom")
    Observable<JMChartResp> appointChatRoom(@QueryMap Map<String, Object> map);

//    @GET("JMessage/cancelChatRoom")
//    Observable<NetResult> cancelChatRoom(@Query("userName") String userName,@Query("roomId") long roomId);

    @GET("JMessage/exitChatRoom")
    Observable<NetResult<BChatRoom>> exitChatRoom(@QueryMap Map<String, Object> map);

    @GET("JMessage/commitChatRoomResult")
    Observable<NetResult> commitChatRoomResult(@QueryMap Map<String, Object> map);

    @GET("JMessage/startChatRoom")
    Observable<NetResult<BChatRoom>> startChatRoom(@Query("roomId") long roomId);

    @GET("JMessage/deleteChatRoom")
    Observable<NetResult<BChatRoom>> deleteChatRoom(@QueryMap Map<String, Object> map);

    @GET("JMessage/getChatRoomByUser")
    Observable<NetResult<BChatRoom>> getChatRoomByUser(@Query("userName") String userName);

    @GET("JMessage/exitChartRoom")
    Observable<JMChartResp> exitChartRoom(@QueryMap Map<String, Object> map);

    @GET("JMessage/deleteChartRoom")
    Observable<JMChartResp> deleteChartRoom(@QueryMap Map<String, Object> map);

    @GET("JMessage/joinChatRoom")
    Observable<NetResult<BChatRoom>> joinChatRoom(@QueryMap Map<String, Object> map);

    @GET("JMessage/enterChatRoom")
    Observable<NetResult<BChatRoom>> enterChatRoom(@QueryMap Map<String, Object> map);

    @GET("JMessage/getChatRoomMember")
    Observable<NetResult<List<Member>>> getChatRoomMember(@QueryMap Map<String, Object> map);

    @GET("JMessage/leaveChatRoom")
    Observable<NetResult> leaveChatRoom(@QueryMap Map<String, Object> map);

    @GET("User/getBlackUserByName")
    Observable<NetResult<BBlackUser>> getBlackUserByName(@QueryMap Map<String, Object> map);

    @Multipart
    @POST("User/uploadHeadImage")
    Call<UserResp> uploadFile(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file);

}
