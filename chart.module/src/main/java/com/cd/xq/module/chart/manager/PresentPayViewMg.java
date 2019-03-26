package com.cd.xq.module.chart.manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.module.chart.beans.BGetPayItem;
import com.cd.xq.module.chart.beans.BMakePayOrder;
import com.cd.xq.module.chart.beans.BusPaySuccessParam;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq_chart.module.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 赠送礼物的View Manager
 * Created by Administrator on 2019/3/24.
 */

public class PresentPayViewMg {
    private View mRootView;
    private Activity mActivity;

    private Button mBtnBack;
    private RecyclerView mRecyclerView;
    private TextView mTextBalance;
    private Button mBtnConsumeHis;
    private MyAdapter myAdapter;
    private ChatRequestApi mApi;
    private ArrayList<BGetPayItem> mDataList;

    public PresentPayViewMg(Activity activity) {
        mActivity = activity;
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_present_charge,null);
        mApi = NetWorkMg.newRetrofit().create(ChatRequestApi.class);

        mBtnBack = mRootView.findViewById(R.id.btn_back);
        mRecyclerView = mRootView.findViewById(R.id.recycler_charge);
        mTextBalance = mRootView.findViewById(R.id.text_balance);
        mBtnConsumeHis = mRootView.findViewById(R.id.btn_consume_history);

        init();

        //获取支付项
        requestPayItem();
    }

    private void init() {
        mDataList = new ArrayList<>();

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootView.setVisibility(View.GONE);
            }
        });
        mBtnConsumeHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.cd.xq.intent.action.MyConsumeHistoryActivity");
                mActivity.startActivity(intent);
            }
        });

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootView.setVisibility(View.GONE);
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        myAdapter = new MyAdapter();
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                    .State state) {
                int index = mRecyclerView.getChildAdapterPosition(view);
                outRect.left = mActivity.getResources().getDimensionPixelOffset(R.dimen.dp_10);

                if(index > 2) {
                    outRect.top = mActivity.getResources().getDimensionPixelOffset(R.dimen.dp_15);
                }else {
                    outRect.top = 0;
                }
            }
        });
        mRecyclerView.setAdapter(myAdapter);
    }

    /**
     * 获取支付项
     */
    private void requestPayItem() {
        mApi.getPayItem()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetPayItem>>>() {
                    @Override
                    public void accept(NetResult<List<BGetPayItem>> listNetResult) throws Exception {
                        if(listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(mActivity.getApplicationContext(),listNetResult.getMsg(),false);
                            Log.e("requestPayItem--" + listNetResult.getMsg());
                            return;
                        }
                        mDataList.clear();
                        mDataList.addAll(listNetResult.getData());
                        myAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(mActivity.getApplicationContext(),throwable.toString(),false);
                        Log.e("requestPayItem--" + throwable.toString());
                    }
                });
    }

    /**
     * 模拟支付回调
     */
    private void requestHandlePayCallback(BMakePayOrder bean) {
        mApi.handlePayCallback(bean.getOrder_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult>() {
                    @Override
                    public void accept(NetResult netResult) throws
                            Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(mActivity.getApplicationContext(),"支付回调失败", false);
                            Log.e("requestHandlePayCallback--" + netResult.getMsg());
                            return;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(mActivity.getApplicationContext(), throwable.toString(), false);
                        Log.e("requestHandlePayCallback--" + throwable.toString());
                    }
                });
    }

    /**
     * 下订单
     * @param bean
     */
    private void requestMakePayOrder(BGetPayItem bean) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("payType",1);
        params.put("coin",bean.getCoin());
        params.put("money",bean.getMoney());
        mApi.makePayOrder(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BMakePayOrder>>() {
                    @Override
                    public void accept(NetResult<BMakePayOrder> bMakePayOrderNetResult) throws Exception {
                        if(bMakePayOrderNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(mActivity.getApplicationContext(),"下单失败",false);
                            Log.e("requestMakePayOrder--" + bMakePayOrderNetResult.getMsg());
                            return;
                        }
                        Tools.toast(mActivity.getApplicationContext(),"下单成功",false);
                        //模拟支付回调
                        requestHandlePayCallback(bMakePayOrderNetResult.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(mActivity.getApplicationContext(),throwable.toString(),false);
                        Log.e("requestMakePayOrder--" + throwable.toString());
                    }
                });
    }

    public void setBalance() {
        mTextBalance.setText("余额：" + String.valueOf(DataManager.getInstance().getUserInfo().getBalance()));
    }

    public void show() {
        mRootView.setVisibility(View.VISIBLE);
        setBalance();
    }

    public View getRootView() {
        return mRootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusParam param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_PAY_SUCCESS) {
            //支付成功
            mTextBalance.setText(String.valueOf(((BusPaySuccessParam)param.getData()).getBalance()));
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textCoin;
        public TextView textMoney;

        public MyViewHolder(View itemView) {
            super(itemView);

            textCoin = itemView.findViewById(R.id.text_coin);
            textMoney = itemView.findViewById(R.id.text_money);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(mActivity)
                    .inflate(R.layout.layout_recycler_gift_pay_item,parent,false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final BGetPayItem bean = mDataList.get(position);
            holder.textCoin.setText(String.valueOf(bean.getCoin() + bean.getBonus()));
            holder.textMoney.setText("¥ " + String.valueOf(bean.getMoney()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //下订单
                    requestMakePayOrder(bean);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

    /**
     * 销毁
     */
    public void onDestroy() {
    }

}
