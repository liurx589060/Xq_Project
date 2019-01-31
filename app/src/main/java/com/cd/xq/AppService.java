package com.cd.xq;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cd.xq.beans.JMOnlineParam;
import com.cd.xq.manager.AppDataManager;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.JMSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.JMRestApi;
import com.cd.xq.network.XqRequestApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jmessage.support.qiniu.android.utils.Json;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2019/1/27.
 */

public class AppService extends Service {
    public static final String PRE = "AppService";
    private XqRequestApi mXqApi;
    private Disposable mDisposable;

    private boolean isDoCheckOnLine = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mXqApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        EventBus.getDefault().register(this);
        JMessageClient.registerEventReceiver(this);

        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        JMessageClient.unRegisterEventReceiver(this);

        if(mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void init() {

    }

    /**
     * 获取好友列表
     */
    private void getFriendList() {
        if(!DataManager.getInstance().getUserInfo().isOnLine()) {
            return;
        }

        mXqApi.getFriendListByUser(DataManager.getInstance().getUserInfo().getUser_name())
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<UserInfoBean>>>() {
                    @Override
                    public void accept(NetResult<List<UserInfoBean>> listNetResult) throws
                            Exception {
                        if (listNetResult.getStatus() != XqErrorCode.SUCCESS && listNetResult.getStatus() !=
                                XqErrorCode.ERROR_NO_DATA) {
                            Log.e(PRE ,"getFriendList--" + listNetResult.getMsg());
                            return;
                        }

                        if (listNetResult.getData() == null || listNetResult.getData().size() == 0) {
                            return;
                        }
                        AppDataManager.getInstance().getFriendList().clear();
                        AppDataManager.getInstance().getFriendList().addAll(listNetResult.getData());
                        if(!isDoCheckOnLine) {
                            //checkUserOnline();
                            userStat();
                            isDoCheckOnLine = true;
                        }

                        //告知好友已经上线
                        checkUserOnline(false,true);

                        //发送需要更新好友列表状态
                        eventBusToUpdateFriend();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(PRE ,"getFriendList--" + throwable.toString());
                    }
                });
    }

    private void checkUserOnline(final boolean isInterval, final boolean isOnLine) {
        final int period = 60*2;
        Observable.interval(0,period,TimeUnit.SECONDS)
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        if(isInterval) {
                            return true;
                        }else {
                            return aLong< 1;
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Long, ObservableSource<UserInfoBean>>() {
                    @Override
                    public ObservableSource<UserInfoBean> apply(Long aLong) throws Exception {
                        return Observable.fromIterable(AppDataManager.getInstance().getFriendList());
                    }
                })
                .subscribe(new Consumer<UserInfoBean>() {
                    @Override
                    public void accept(UserInfoBean userInfoBean) throws Exception {
                        JMSendBean sendBean = new JMSendBean();
                        sendBean.setCode(JMSendBean.JM_SEND_USER_CHECK_ONLINE);
                        sendBean.setTargetUserName(userInfoBean.getUser_name());
                        sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
                        JMOnlineParam param = new JMOnlineParam();
                        param.setType(JMOnlineParam.TYPE_SEND);
                        param.setOnLine(isOnLine);
                        sendBean.setData(param);
                        JMsgSender.sendTextMessage(sendBean);
                    }
                });
    }

    private void eventBusToUpdateFriend() {
        //发送需要更新好友列表状态
        EventBusParam param = new EventBusParam();
        param.setEventBusCode(EventBusParam.EVENT_BUS_UPDATE_FRIENDLIST);
        EventBus.getDefault().post(param);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFriendList(EventBusParam param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_GET_FRIENDLIST) {
            getFriendList();
            //不让继续传递
            EventBus.getDefault().cancelEventDelivery(param);
        }
    }

//    /**
//     * 接收普通消息
//     * @param event
//     */
//    public void onEventMainThread(MessageEvent event){
//        String message = event.getMessage().getContent().toJson();
//        String text = null;
//        try {
//            JSONObject object = new JSONObject(message);
//            text = object.getString("text");
//        }catch (Exception e) {
//            Log.e(PRE,"onEventMainThread--" + e.toString());
//            return;
//        }
//
//        JMSendBean<JMOnlineParam> bean = new Gson().fromJson(text,new TypeToken<JMSendBean<JMOnlineParam>>(){}.getType());
//        if(bean.getCode() == JMSendBean.JM_SEND_USER_CHECK_ONLINE) {
//            if(bean.getData().getType() == JMOnlineParam.TYPE_SEND) {
//                //回复
//                JMSendBean sendBean = new JMSendBean();
//                sendBean.setCode(JMSendBean.JM_SEND_USER_CHECK_ONLINE);
//                sendBean.setTargetUserName(bean.getFromUserName());
//                sendBean.setFromUserName(bean.getTargetUserName());
//                JMOnlineParam param = new JMOnlineParam();
//                param.setOnLine(true);
//                param.setType(JMOnlineParam.TYPE_RESPONSE);
//                sendBean.setData(param);
//                JMsgSender.sendTextMessage(sendBean);
//            }else if(bean.getData().getType() == JMOnlineParam.TYPE_RESPONSE) {
//                String fromUserName = bean.getFromUserName();
//                for(int i = 0 ; i < AppDataManager.getInstance().getFriendList().size() ; i++) {
//                    UserInfoBean infoBean = AppDataManager.getInstance().getFriendList().get(i);
//                    if(infoBean.getUser_name().equals(fromUserName)
//                            && infoBean.isOnLine() != bean.getData().isOnLine()) {
//                        infoBean.setOnLine(bean.getData().isOnLine());
//                        //发送需要更新好友列表状态
//                        eventBusToUpdateFriend();
//                    }
//                }
//            }
//        }
//    }

    /**
     * 接收普通消息
     * @param event
     */
    public void onEventMainThread(MessageEvent event){
        String message = event.getMessage().getContent().toJson();
        String text = null;
        try {
            JSONObject object = new JSONObject(message);
            text = object.getString("text");
        }catch (Exception e) {
            Log.e(PRE,"onEventMainThread--" + e.toString());
            return;
        }

        JMSendBean<JMOnlineParam> bean = new Gson().fromJson(text,new TypeToken<JMSendBean<JMOnlineParam>>(){}.getType());
        if(bean.getCode() == JMSendBean.JM_SEND_USER_CHECK_ONLINE) {
            if(bean.getData().getType() == JMOnlineParam.TYPE_SEND) {
                //更新好友在线状态
                String fromUserName = bean.getFromUserName();
                UserInfoBean infoBean = getUserInfoFromFriendListByName(fromUserName);
                if(infoBean != null && (infoBean.isOnLine() != bean.getData().isOnLine())) {
                    infoBean.setOnLine(bean.getData().isOnLine());
                    //发送需要更新好友列表状态
                    eventBusToUpdateFriend();
                }
            }
        }
    }

    private void userStat() {
        final JMRestApi restApi = NetWorkMg.newJMRestApiRetrofit().create(JMRestApi.class);
        List<String> list = new ArrayList<>();
        for(int i = 0 ; i < AppDataManager.getInstance().getFriendList().size() ; i++) {
            list.add(AppDataManager.getInstance().getFriendList().get(i).getUser_name());
        }
        String jsonBody = new Gson().toJson(list);
        final RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonBody);
        final int period = 60*2;
        mDisposable = Observable.interval(0,period,TimeUnit.SECONDS)
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong<1;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Long, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Long aLong) throws Exception {
                        return restApi.userStat(body);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        try{
                            JSONArray jsonArray = new JSONArray(s);
                            boolean isUpdateOnLine = false;
                            for(int i = 0 ; i < jsonArray.length() ; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String userName = object.getString("username");
                                JSONArray deviceArrary = object.getJSONArray("devices");
                                boolean isOnLine = false;
                                for(int j = 0 ; j < deviceArrary.length() ; j++) {
                                    JSONObject deviceObject = deviceArrary.getJSONObject(j);
                                    String platform = deviceObject.getString("platform");
                                    if(platform.equalsIgnoreCase("a")
                                            || platform.equalsIgnoreCase("i")) {
                                        boolean bOnLine = deviceObject.getBoolean("online");
                                        boolean bLogin = deviceObject.getBoolean("login");
                                        if(bOnLine && bLogin) {
                                            isOnLine = true;
                                        }
                                    }
                                }

                                UserInfoBean bean = getUserInfoFromFriendListByName(userName);
                                if(bean.isOnLine() != isOnLine) {
                                    isUpdateOnLine = true;
                                    bean.setOnLine(isOnLine);
                                }
                            }

                            //是否发送更新列表
                            if(isUpdateOnLine) {
                                eventBusToUpdateFriend();
                            }
                        }catch (Exception e) {
                            Log.e("userStat--" + e.toString());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("userStat--" + throwable.toString());
                    }
                });
    }

    private UserInfoBean getUserInfoFromFriendListByName(String userName) {
        for(int i = 0 ; i < AppDataManager.getInstance().getFriendList().size() ; i++) {
            UserInfoBean bean = AppDataManager.getInstance().getFriendList().get(i);
            if(bean.getUser_name().equals(userName)) {
                return bean;
            }
        }
        return null;
    }
}
