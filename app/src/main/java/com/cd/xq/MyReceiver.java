package com.cd.xq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cd.xq.beans.BusChatRoomParam;
import com.cd.xq.module.chart.beans.BusPaySuccessParam;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.tools.Log;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				//发送app内广播
//				Intent intent1 = new Intent(AppConstant.BROADCAST_JPUSH_MESSAGE_ACTION);
//				Bundle bundle1 = new Bundle();
//				bundle1.putString("message",bundle.getString(JPushInterface.EXTRA_MESSAGE));
//				intent1.putExtras(bundle1);
//				context.sendBroadcast(intent1);

				String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
				JSONObject object = new JSONObject(msg);
				int nType = object.getInt("type");
				switch (nType) {
					case EventBusParam.EVENT_BUS_CHATROOM_CREATE:
					case EventBusParam.EVENT_BUS_CHATROOM_DELETE:
					case EventBusParam.EVENT_BUS_CHATROOM_JOIN:
					case EventBusParam.EVENT_BUS_CHATROOM_EXIT: {
						EventBusParam<BusChatRoomParam> param = new EventBusParam();
						param.setEventBusCode(nType);
						BusChatRoomParam chatRoomParam = new Gson().fromJson(object.getJSONObject("data").toString()
								,BusChatRoomParam.class);
						param.setData(chatRoomParam);
						EventBus.getDefault().post(param);
					}
					break;
					case EventBusParam.EVENT_BUS_PAY_SUCCESS: {
						EventBusParam<BusPaySuccessParam> param = new EventBusParam();
						param.setEventBusCode(nType);
						BusPaySuccessParam dataParam = new Gson().fromJson(object.getJSONObject("data").toString()
								,BusPaySuccessParam.class);
						param.setData(dataParam);
						EventBus.getDefault().post(param);
					}
				}
			}
		} catch (Exception e){
			Log.e(TAG + "--" + e.toString());
		}

	}
}
