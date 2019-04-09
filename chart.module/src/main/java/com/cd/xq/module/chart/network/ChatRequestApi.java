package com.cd.xq.module.chart.network;

import com.cd.xq.module.chart.beans.BConsumeGift;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.beans.BGetPayItem;
import com.cd.xq.module.chart.beans.BGetReportItem;
import com.cd.xq.module.chart.beans.BMakePayOrder;
import com.cd.xq.module.util.beans.NetResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2018/12/9.
 */

public interface ChatRequestApi {
    @GET("User/reportUser")
    Observable<NetResult<String>> reportUser(@QueryMap Map<String, Object> map);

    @GET("User/getReportItems")
    Observable<NetResult<List<BGetReportItem>>> getReportItems();

    @GET("Pay/getPayItem")
    Observable<NetResult<List<BGetPayItem>>> getPayItem();

    @GET("Pay/getGiftItem")
    Observable<NetResult<List<BGetGiftItem>>> getGiftItem(@Query("position") int position);

    @GET("Pay/buyGiftByCoin")
    Observable<NetResult<Long>> buyGiftByCoin(@QueryMap Map<String, Object> map);

    @GET("Pay/makePayOrder")
    Observable<NetResult<BMakePayOrder>> makePayOrder(@QueryMap Map<String, Object> map);

    @GET("Pay/getGiftList")
    Observable<NetResult<List<BGetGiftItem>>> getGiftList(@Query("userName") String userName);

    @GET("Pay/consumeGift")
    Observable<NetResult<BConsumeGift>> consumeGift(@QueryMap Map<String, Object> map);

    /**
     * 模拟
     * @param orderId
     * @return
     */
    @GET("Pay/handlePayCallback")
    Observable<NetResult> handlePayCallback(@Query("orderId") String orderId);
}
