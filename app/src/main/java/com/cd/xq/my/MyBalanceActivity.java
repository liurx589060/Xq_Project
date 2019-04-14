package com.cd.xq.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.bj.paysdk.listener.PayResultListener;
import com.base.bj.paysdk.utils.TrPay;
import com.cd.xq.R;
import com.cd.xq.module.chart.beans.BGetPayItem;
import com.cd.xq.module.chart.beans.BMakePayOrder;
import com.cd.xq.module.chart.beans.BusPaySuccessParam;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.util.AppConfig;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.base.bj.paysdk.domain.TrPayResult.RESULT_CODE_SUCC;

/**
 * 余额
 * Created by Administrator on 2019/3/16.
 */

public class MyBalanceActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.text_consume_history)
    TextView textConsumeHistory;
    @BindView(R.id.text_balance)
    TextView textBalance;
    @BindView(R.id.relayout_charge)
    RelativeLayout relayoutCharge;
    @BindView(R.id.recyclerView_goods)
    RecyclerView recyclerViewGoods;

    private XqRequestApi mApi;
    private ChatRequestApi mChatApi;
    private RequestApi mCommonApi;
    private ArrayList<BGetPayItem> mDataList;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_balance);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        mChatApi = NetWorkMg.newRetrofit().create(ChatRequestApi.class);
        mCommonApi = NetWorkMg.newRetrofit().create(RequestApi.class);

        init();
    }

    private void init() {
        mDataList = new ArrayList<>();

        LinearLayoutManager layoutManager = new  LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false);
        recyclerViewGoods.setLayoutManager(layoutManager);
        recyclerViewGoods.setNestedScrollingEnabled(false);
        mAdapter = new MyAdapter();
        recyclerViewGoods.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取支付项
        requestPayItem();
        requestUserInfo();
    }

    private void requestUserInfo() {
        mCommonApi.getUserInfoByUserName(DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<UserResp>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        if(userResp.getStatus() == XqErrorCode.SUCCESS && userResp.getData() != null) {
                            DataManager.getInstance().setJmUserName(userResp.getData().getUser_name());
                            DataManager.getInstance().setUserInfo(userResp.getData());
                            textBalance.setText(String.valueOf(DataManager.getInstance().getUserInfo().getBalance()));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("" + throwable.toString());
                    }
                });
    }

    /**
     * 获取支付项
     */
    private void requestPayItem() {
        mChatApi.getPayItem()
                .compose(this.<NetResult<List<BGetPayItem>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetPayItem>>>() {
                    @Override
                    public void accept(NetResult<List<BGetPayItem>> listNetResult) throws Exception {
                        if(listNetResult.getStatus() == XqErrorCode.SUCCESS && listNetResult.getData() != null) {
                            mDataList.clear();
                            mDataList.addAll(listNetResult.getData());
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("" + throwable.toString());
                    }
                });
    }

    @OnClick({R.id.btn_back, R.id.text_consume_history, R.id.relayout_charge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.text_consume_history:
            {
                Intent intent = new Intent(MyBalanceActivity.this,MyConsumeHistoryActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.relayout_charge:
            {
                Intent intent = new Intent(MyBalanceActivity.this,MyChargeHistoryActivity.class);
                startActivity(intent);
            }
                break;
        }
    }

    /**
     * 模拟支付回调
     */
    private void requestHandlePayCallback(BMakePayOrder bean) {
        mChatApi.handlePayCallback(bean.getOrder_id())
                .compose(this.<NetResult>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult>() {
                    @Override
                    public void accept(NetResult netResult) throws
                            Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(),"支付回调失败", false);
                            Log.e("requestHandlePayCallback--" + netResult.getMsg());
                            return;
                        }
                        Tools.toast(getApplicationContext(),"支付成功",false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(), throwable.toString(), false);
                        Log.e("requestHandlePayCallback--" + throwable.toString());
                    }
                });
    }

    /**
     * 下订单
     * @param bean
     */
    private void requestMakePayOrder(final BGetPayItem bean) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("payType",1);
        params.put("coin",bean.getCoin());
        params.put("money",bean.getMoney());
        mChatApi.makePayOrder(params)
                .compose(this.<NetResult<BMakePayOrder>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BMakePayOrder>>() {
                    @Override
                    public void accept(NetResult<BMakePayOrder> bMakePayOrderNetResult) throws Exception {
                        if(bMakePayOrderNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(),"下单失败",false);
                            Log.e("requestMakePayOrder--" + bMakePayOrderNetResult.getMsg());
                            return;
                        }
                        BMakePayOrder order = bMakePayOrderNetResult.getData();
                        if(AppConfig.isTestPay) {
                            //模拟支付回调
                            requestHandlePayCallback(bMakePayOrderNetResult.getData());
                            return;
                        }
                        //调用TrPay支付
                        String callbackUrl = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/Sample_Mjmz/Pay/handleTrPayCallback";
                        TrPay.getInstance(MyBalanceActivity.this).callPay(bean.getDescription(),
                                order.getOrder_id(),
                                (long) bean.getMoney()/6,
                                "",
                                callbackUrl,
                                DataManager.getInstance().getUserInfo().getUser_name(),
                                new PayResultListener() {
                                    @Override
                                    public void onPayFinish(Context context, String outtradeno, int resultCode
                                            , String resultString, int payType, Long amount, String tradename) {
                                        if(resultCode == RESULT_CODE_SUCC.getId()) {
                                            //成功
                                            Tools.toast(getApplicationContext(),"支付成功",false);
                                        }else {
                                            Tools.toast(getApplicationContext(),"支付失败",false);
                                        }
                                        StringBuilder builder = new StringBuilder();
                                        builder.append("outtradeno=" + outtradeno)
                                                .append("\n")
                                                .append("resultCode=" + resultCode)
                                                .append("\n")
                                                .append("resultString=" + resultString)
                                                .append("\n")
                                                .append("payType=" + payType)
                                                .append("\n")
                                                .append("amount=" + amount)
                                                .append("\n")
                                                .append("tradename=" + tradename);
                                        Log.e("requestMakePayOrder--" + builder.toString());
                                    }
                                });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(),throwable.toString(),false);
                        Log.e("requestMakePayOrder--" + throwable.toString());
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusParam param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_PAY_SUCCESS) {
            //支付成功
            textBalance.setText(String.valueOf(((BusPaySuccessParam)param.getData()).getBalance()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textCoinCount;
        public Button btnPay;

        public MyViewHolder(View itemView) {
            super(itemView);

            textCoinCount = itemView.findViewById(R.id.text_coin_count);
            btnPay = itemView.findViewById(R.id.btn_coin_pay);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyBalanceActivity.this)
                    .inflate(R.layout.layout_balance_recycler_item,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final BGetPayItem bean = mDataList.get(position);
            holder.textCoinCount.setText("" + (bean.getCoin() + bean.getBonus()));
            holder.btnPay.setText("¥ " + bean.getMoney()/10.0f);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //充值
                    requestMakePayOrder(bean);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
