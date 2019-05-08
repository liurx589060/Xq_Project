package com.cd.xq.frame;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
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
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.jmessage.JMChartResp;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.DateUtils;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.my.MyGiftBuyActivity;
import com.cd.xq.network.XqRequestApi;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 创建房间
 * Created by Administrator on 2019/4/10.
 */

public class CreateRoomActivity extends BaseActivity {
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
    @BindView(R.id.edit_lady_count)
    EditText editLadyCount;
    @BindView(R.id.btn_start_time)
    Button btnStartTime;
    @BindView(R.id.edit_description)
    EditText editDescription;

    private boolean mIsPublic = true;
    private XqRequestApi mApi;
    private RequestApi mCommonApi;
    private ChatRequestApi mChatApi;
    private TimePickerView mDialogAll;
    private String mStartTime;

    private String mTXPushAddress = "";
    private String mTXPlayerAddress = "";
    private int mPushAddressType = 0;

    private Calendar mStartCalendar;
    private Calendar mEndCalendar;

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
        editTitle.setHint("一起来相亲吧");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_public) {
                    mIsPublic = true;
                } else {
                    mIsPublic = false;
                }
            }
        });

        mStartCalendar = Calendar.getInstance();
        mEndCalendar = Calendar.getInstance();
        mStartCalendar.add(Calendar.MINUTE,5);
        mEndCalendar.setTimeInMillis(mStartCalendar.getTimeInMillis());
        mEndCalendar.add(Calendar.HOUR_OF_DAY,2);
        initTimeDialog(mStartCalendar,mEndCalendar);
    }

    /**
     * 初始化TimeDialog
     */
    private void initTimeDialog(Calendar startDate,Calendar endDate) {
        boolean month = false;
        if(endDate.get(Calendar.MONTH) != startDate.get(Calendar.MONTH)) {
            month = true;
        }
        mDialogAll = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mStartTime = DateUtils.timeStampToStr(date.getTime() / 1000, "yyyy-MM-dd HH:mm:00");
                btnStartTime.setText("已预约的时间\n" + mStartTime);
            }
        })
                .setType(new boolean[]{false, month, true, true, true, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确认")//确认按钮文字
                .setDate(startDate)
                .setRangDate(startDate, endDate)
                .setContentTextSize(18)
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.5f)
                .setTextXOffset(0, 0, 40, 40, 40, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {

                    }
                })
                .build();
    }

    private void toCommit() {
        if (TextUtils.isEmpty(mStartTime)) {
            Tools.toast(getApplicationContext(), "请选择开始开始时间", false);
            return;
        }

        //创建房间
        getLoadingDialog().show();
        mApi.checkRoomExpiry(DataManager.getInstance().getUserInfo().getUser_name(), 1)
                .compose(this.<NetResult<BCheckRoomExpiry>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BCheckRoomExpiry>>() {
                    @Override
                    public void accept(NetResult<BCheckRoomExpiry> bCheckRoomExpiry) throws Exception {
                        getLoadingDialog().dismiss();
                        if (bCheckRoomExpiry.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), bCheckRoomExpiry.getMsg(), false);
                            Log.e("checkRoomExpiry--" + bCheckRoomExpiry.getMsg());
                            return;
                        }

                        if (bCheckRoomExpiry.getData().getExpiry() != null) {
                            //有使用中的卡,则直接创建房间
                            String str = "您有使用中的" + bCheckRoomExpiry.getData().getExpiry().getName() +
                                    ",可免费创建房间,截止至" + bCheckRoomExpiry.getData().getExpiry().getEnd_time();
                            Toast toast = Toast.makeText(getApplication(), str, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 0);
                            toast.show();

                            //创建房间
                            createChartRoom();
                        } else {
                            if (bCheckRoomExpiry.getData().getHasCard() == 1) {
                                //有未使用的卡，提示是否使用卡
                                doCreateUseCardDialog(bCheckRoomExpiry.getData());
                            } else {
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
                        getLoadingDialog().dismiss();
                    }
                });
    }

    /**
     * 去消费
     *
     * @param item
     * @param handleType
     */
    private void doRequestConsumeGift(final BGetGiftItem item, int handleType) {
        //用钻石消费
        if (handleType == 1 && !ChatTools.checkBalance(this, item.getCoin())) return;
        requestConsumeGift(item, handleType);
    }

    private void requestConsumeGift(final BGetGiftItem item, final int handleType) {
        getLoadingDialog().show();
        HashMap<String, Object> params = new HashMap<>();
        params.put("userName", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("giftId", item.getGift_id());
        params.put("coin", item.getCoin());
        params.put("toUser", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("handleType", handleType);  //消费方式
        mChatApi.consumeGift(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BConsumeGift>>() {
                    @Override
                    public void accept(NetResult<BConsumeGift> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            if (netResult.getStatus() == XqErrorCode.ERROR_LACK_STOCK) {
                                //余额不足
                                //更新余额
                                DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                                doRequestConsumeGift(item, handleType);
                            } else {
                                Tools.toast(getApplicationContext(), netResult.getMsg(), false);
                            }
                            Log.e("requestConsumeGift--" + netResult.getMsg());
                            getLoadingDialog().dismiss();
                            return;
                        }

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
                        getLoadingDialog().dismiss();
                    }
                });
    }

    /**
     * 创建是否使用建房卡
     *
     * @param checkRoomExpiry
     */
    private void doCreateUseCardDialog(final BCheckRoomExpiry checkRoomExpiry) {
        String text = "您有未使用的" + checkRoomExpiry.getGift().getName() + ",使用后可免费创建房间"
                + checkRoomExpiry.getGift().getValue() + "小时"
                + "，或者使用" + checkRoomExpiry.getTargetGift().getCoin() + "钻石";
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRoomActivity.this)
                .setTitle("提示")
                .setMessage(text)
                .setPositiveButton("使用卡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //使用卡,包裹使用
                        doRequestConsumeGift(checkRoomExpiry.getGift(), 2);
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
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(), 1);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 创建是否购买建房卡
     *
     * @param checkRoomExpiry
     */
    private void doCreateCoinDialog(final BCheckRoomExpiry checkRoomExpiry) {
        String text = "您是否花费" + checkRoomExpiry.getGift().getCoin() + "钻石购买" + checkRoomExpiry.getGift().getName() + ",使用后可免费创建房间"
                + checkRoomExpiry.getGift().getValue() + "小时"
                + "，或者使用" + checkRoomExpiry.getTargetGift().getCoin() + "钻石创建房间";
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRoomActivity.this)
                .setTitle("提示")
                .setMessage(text)
                .setPositiveButton("去购买", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //购买卡，调转到购买页面
                        Intent intent = new Intent(CreateRoomActivity.this, MyGiftBuyActivity.class);
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
                        doRequestConsumeGift(checkRoomExpiry.getTargetGift(), 1);
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
            mTXPushAddress = NetWorkMg.getCameraUrl() + "_" + DataManager.getInstance().getUserInfo().getUser_name();
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

    private void createChartRoom() {
        UserInfoBean userInfo = new UserInfoBean();
        userInfo.setUser_name(DataManager.getInstance().getUserInfo().getUser_name());
        userInfo.setRole_type(DataManager.getInstance().getUserInfo().getRole_type());
        userInfo.setGender(DataManager.getInstance().getUserInfo().getGender());
        userInfo.setLevel(DataManager.getInstance().getUserInfo().getLevel());
        String ladyCount = editLadyCount.getText().toString();
        try {
            if (TextUtils.isEmpty(ladyCount)) {
                userInfo.setLimitLady(AppConstant.CHATROOM_LIMIT_LADY_COUNT);
            } else {
                int count = Integer.parseInt(ladyCount);
                if (count < 2 || count > 10) {
                    Tools.toast(getApplicationContext(), "女嘉宾人数需是2--10人", true);
                    return;
                }
                userInfo.setLimitLady(count);
            }
        } catch (Exception e) {
            Tools.toast(getApplicationContext(), "女嘉宾人数输入有误", true);
            Log.e("createChartRoom--" + e.toString());
            return;
        }
        userInfo.setLimitLevel(-1);

        setLiveAddress();

        Map<String, Object> params = new HashMap<>();
        params.put("userName", userInfo.getUser_name());
        params.put("gender", userInfo.getGender());
        params.put("level", userInfo.getLevel());
        params.put("limitLevel", userInfo.getLimitLevel());
        params.put("limitLady", userInfo.getLimitLady());
        params.put("limitMan", userInfo.getLimitMan());
        params.put("limitAngel", userInfo.getLimitAngel());
        params.put("appointTime", mStartTime);
        params.put("pushAddress", Base64.encodeToString(mTXPushAddress.getBytes(), Base64.DEFAULT));
        params.put("playAddress", Base64.encodeToString(mTXPlayerAddress.getBytes(),
                Base64.DEFAULT));
        params.put("public", mIsPublic ? 1 : 0);
        String title = editTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            title = editTitle.getHint().toString();
        }
        params.put("title", title);
        params.put("describe", editDescription.getText().toString());

        if (userInfo.getLimitLady() % 2 != 0) {
            Tools.toast(getApplicationContext(), "请输入偶数", true);
            return;
        }

        getLoadingDialog().show();
        mCommonApi.appointChatRoom(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JMChartResp>() {
                    @Override
                    public void accept(JMChartResp jmChartResp) throws Exception {
                        getLoadingDialog().dismiss();
                        if (jmChartResp == null) {
                            Log.e("jmChartResp is null");
                            Tools.toast(getApplicationContext(), "jmChartResp is null", true);
                            return;
                        }
                        if (jmChartResp.getStatus() != XqErrorCode.SUCCESS) {
                            Log.e(jmChartResp.getMsg());
                            Tools.toast(getApplicationContext(), jmChartResp.getMsg(), true);
                            return;
                        }

                        //退出Activity
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(throwable.toString());
                        Tools.toast(getApplicationContext(), throwable.toString(), true);
                        getLoadingDialog().dismiss();
                    }
                });
    }

    @OnClick({R.id.btn_back, R.id.btn_commit, R.id.btn_start_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_commit:
                toCommit();
                break;
            case R.id.btn_start_time:
                mStartCalendar.setTimeInMillis(System.currentTimeMillis());
                mStartCalendar.add(Calendar.MINUTE,5);
                mEndCalendar.setTimeInMillis(mStartCalendar.getTimeInMillis());
                mEndCalendar.add(Calendar.HOUR_OF_DAY,2);
                if(mEndCalendar.get(Calendar.MONTH) != mStartCalendar.get(Calendar.MONTH)) {
                    initTimeDialog(mStartCalendar,mEndCalendar);
                }

                mDialogAll.show();
                break;
        }
    }
}
