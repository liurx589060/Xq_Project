package com.cd.xq.module.chart.network;

import com.cd.xq.module.chart.beans.BGetReportItem;
import com.cd.xq.module.util.beans.NetResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2018/12/9.
 */

public interface ChatRequestApi {
    @GET("User/reportUser")
    Observable<NetResult<String>> reportUser(@QueryMap Map<String, Object> map);

    @GET("User/getReportItems")
    Observable<NetResult<List<BGetReportItem>>> getReportItems();
}
