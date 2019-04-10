package com.cd.xq.frame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cd.xq.AppConstant;
import com.cd.xq.R;
import com.cd.xq.beans.BCheckRoomExpiry;
import com.cd.xq.module.chart.ChartRoomActivity;
import com.cd.xq.module.chart.beans.BConsumeGift;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.chart.utils.ChatTools;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.my.MyGiftBuyActivity;
import com.cd.xq.network.XqRequestApi;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 加入房间
 * Created by Administrator on 2019/4/10.
 */

public class JoinRoomActivity extends BaseActivity {
    public static final int AC_CHATROOM_REQUEST_CODE = 1000;

    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.edit_title)
    EditText editTitle;
    @BindView(R.id.radio_public)
    RadioButton radioPublic;
    @BindView(R.id.radio_private)
    RadioButton radioPrivate;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    private boolean mIsPublic = true;
    private XqRequestApi mApi;
    private RequestApi mCommonApi;
    private ChatRequestApi mChatApi;

    private String mTXPushAddress = "";
    private String mTXPlayerAddress = "";
    private int mPushAddressType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room_activity);
        ButterKnife.bind(this);
        mApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        mCommonApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        mChatApi = NetWorkMg.newRetrofit().create(ChatRequestApi.class);

        init();
    }

    private void init() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_public) {
                    mIsPublic = true;
                }else {
                    mIsPublic = false;
                }
            }
        });
    }

    private void toCommit() {
        //加入房间
        mApi.checkRoomExpiry(DataManager.getInstance().getUserInfo().getUser_name(),2)
                .compose(this.<NetResult<BCheckRoomExpiry>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BCheckRoomExpiry>>() {
                    @Override
                    public void accept(NetResult<BCheckRoomExpiry> bCheckRoomExpiry) throws Exception {
                        if(bCheckRoomExpiry.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), bCheckRoomExpiry.getMsg(), false);
                            Log.e("checkRoomExpiry--" + bCheckRoomExpiry.getMsg());
                            return;
                        }

                        if(bCheckRoomExpiry.getData().getExpiry() != null) {
                            //有使用中的卡,则直接创建房间
                            String str = "您有使用中的" + bCheckRoomExpiry.getData().getExpiry().getName() +
                                    ",可免费加入房间,剩余次数" + bCheckRoomExpiry.getData().getExpiry().getExpiry_num();
                            Toast toast = Toast.makeText(getApplication(),str,Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,0);
                            toast.show();

                            //创建房间
                            createChartRoom();
                        }else {
                            if(bCheckRoomExpiry.getData().getGift() != null) {
                                //有未使用的卡，提示是否使用卡
                                doCreateUseCardDialog(bCheckRoomExpiry.getData());
                            }else {
                                //没有卡，则提示是否使用钻石创建房间
                                doCreateCoinDialog(bCheckRoomExpiry.getData());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(), throwable.toString(), false);
                        Log.e("checkRoomExpiry--" + throwable.toString());
                    }
                });
    }

    /**
     * 去消费
     * @param item
     * @param handleType
     */
    private void doRequestConsumeGift(final BGetGiftItem item,int handleType) {
        //用钻石消费
        if(handleType == 1 && !ChatTools.checkBalance(this,item.getCoin())) return;
        requestConsumeGift(item,handleType);
    }

    private void requestConsumeGift(final BGetGiftItem item, final int handleType) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("giftId",item.getGift_id());
        params.put("coin",item.getCoin());
        params.put("toUser",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("handleType",handleType);  //消费方式
        mChatApi.consumeGift(params)
                .compose(this.<NetResult<BConsumeGift>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BConsumeGift>>() {
                    @Override
                    public void accept(NetResult<BConsumeGift> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            if(netResult.getStatus() == XqErrorCode.ERROR_LACK_STOCK) {
                                //余额不足
                                doRequestConsumeGift(item,handleType);
                            }else {
                                Tools.toast(getApplicationContext(), netResult.getMsg(), false);
                            }
                            Log.e("requestConsumeGift--" + netResult.getMsg());
                            return;
                        }

                        Tools.toast(getApplicationContext(), "你送出了" + item.getName()
                                , false);
                        //更新余额
                        DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                        //创建房间
                        createChartRoom();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(), throwable.toString(), false);
                        Log.e("requestConsumeGift--" + throwable.toString());
                    }
                });
    }

    /**
     * 创建是否使用建房卡
     * @param checkRoomExpiry
     */
    private void doCreateUseCardDialog(final BCheckRoomExpiry checkRoomExpiry) {
        String text = "您有未使用的" + checkRoomExpiry.getExpiry().getName() + ",使用后可免费创建房间"
                + checkRoomExpiry.getExpiry().getValue() + "小时"
                + "，或者使用" + checkRoomExpiry.getTargetGift().getCoin() + "钻石";
        AlertDialog.Builder builder = new AlertDialog.Builder(JoinRoomActivity.this)
                .setTitle("提示")
                .setMessage(text)
                .setPositiveButton("使用卡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //使用卡,包裹使用
                        doRequestConsumeGift(checkRoomExpiry.getGift(),2);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNeutralButton("使用钻石", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //钻石消费
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(),1);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 创建是否购买建房卡
     * @param checkRoomExpiry
     */
    private void doCreateCoinDialog(final BCheckRoomExpiry checkRoomExpiry) {
        String text = "您是否购买" + checkRoomExpiry.getExpiry().getName() + ",使用后可免费创建房间"
                + checkRoomExpiry.getExpiry().getValue() + "小时"
                + "，或者使用" + checkRoomExpiry.getTargetGift().getCoin() + "钻石";
        AlertDialog.Builder builder = new AlertDialog.Builder(JoinRoomActivity.this)
                .setTitle("提示")
                .setMessage(text)
                .setPositiveButton("去购买", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //购买卡，调转到购买页面
                        Intent intent = new Intent(JoinRoomActivity.this, MyGiftBuyActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNeutralButton("使用钻石", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //钻石消费
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(),1);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String byteArrayToHexString(byte[] data) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                'd', 'e', 'f'};
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    /*
     * KEY+ stream_id + txTime
	 */
    private void setLiveAddress() {
        if (mPushAddressType == 0) {
            //本地
            mTXPushAddress = NetWorkMg.getCameraUrl();
            mTXPlayerAddress = mTXPushAddress;
        } else if (mPushAddressType == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 24);
            long txTime = calendar.getTimeInMillis() / 1000;
            String input = new StringBuilder().
                    append(Constant.TX_LIVE_PUSH_KEY).
                    append(Constant.TX_LIVE_BIZID + "_"
                            + String.valueOf(DataManager.getInstance().getUserInfo().getUser_id())).
                    append(Long.toHexString(txTime).toUpperCase()).toString();
            Log.d("yy", input);

            String txSecret = null;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                txSecret = byteArrayToHexString(
                        messageDigest.digest(input.getBytes("UTF-8")));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e(e.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(e.toString());
            }

            mTXPlayerAddress = "rtmp://" + Constant.TX_LIVE_BIZID + ".liveplay.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getUserInfo()
                    .getUser_id();

            String ip = "rtmp://" + Constant.TX_LIVE_BIZID + ".livepush.myqcloud.com/live/"
                    + Constant.TX_LIVE_BIZID + "_" + DataManager.getInstance().getUserInfo()
                    .getUser_id()
                    + "?bizid=" + Constant.TX_LIVE_BIZID;
            mTXPushAddress = new StringBuilder().
                    append(ip).
                    append("&").
                    append("txSecret=").
                    append(txSecret).
                    append("&").
                    append("txTime=").
                    append(Long.toHexString(txTime).toUpperCase()).
                    toString();
        }
        Log.i("yy", "TXPlayerAddress=" + mTXPlayerAddress);
        Log.i("yy", "TXPushAddress=" + mTXPushAddress);
    }

    /**
     * 加入房间
     * @param roomRoleType
     * @param roomId
     */
    private void joinChartRoom(int roomRoleType, long roomId) {
        UserInfoBean userInfo = DataManager.getInstance().getUserInfo();

        Map<String, Object> params = new HashMap<>();
        params.put("userName", userInfo.getUser_name());
        params.put("gender", userInfo.getGender());
        params.put("level", userInfo.getLevel());
        params.put("roleType", userInfo.getRole_type());
        params.put("roomRoleType", roomRoleType);
        if ((Integer) params.get("roomRoleType") == Constant.ROOM_ROLETYPE_ONLOOKER) {
            params.put("roomId", roomId);
        }
        mCommonApi.joinChartRoom(params)
                .subscribeOn(Schedulers.io())
                .compose(this.<JMChartResp>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        if (jmChartResp == null) {
                            Log.e("jmChartResp is null");
                            Tools.toast(getActivity(), "jmChartResp is null", true);
                            return;
                        }
                        if (jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e(jmChartResp.getMsg());
                            Tools.toast(getActivity(), jmChartResp.getMsg(), true);
                            return;
                        }
                        DataManager.getInstance().setChartData(jmChartResp.getData());
                        Intent intent = new Intent(getActivity(), ChartRoomActivity.class);
                        if (DataManager.getInstance().getSelfMember().getRoomRoleType() ==
                                Constant.ROOM_ROLETYPE_PARTICIPANTS) {
                            getActivity().startActivityForResult(intent, AC_CHATROOM_REQUEST_CODE);
                        } else {
                            getActivity().startActivity(intent);
                        }
                        //发送聊天室信息
                        sendChartRoomMessage();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(getActivity(), throwable.toString(), true);
                    }
                });
    }

    @OnClick({R.id.btn_back, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_commit:
                toCommit();
                break;
        }
    }
}
