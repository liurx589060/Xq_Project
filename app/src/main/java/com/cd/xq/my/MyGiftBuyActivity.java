package com.cd.xq.my;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.R;
import com.cd.xq.beans.BGetGiftItem;
import com.cd.xq.beans.BusPaySuccessParam;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.common.MultiItemDivider;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 购买礼物
 * Created by Administrator on 2019/3/21.
 */

public class MyGiftBuyActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private MyAdapter myAdapter;
    private XqRequestApi mApi;
    private ArrayList<BGetGiftItem> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gift_buy);
        ButterKnife.bind(this);
        mApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);

        init();
    }

    private void init() {
        mDataList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL,
                false);
        MultiItemDivider divider = new MultiItemDivider(this, DividerItemDecoration
                .VERTICAL,
                ContextCompat.getDrawable(this, R.drawable.shape_gift_buy_recycler_divider));
        divider.setDividerMode(MultiItemDivider.INSIDE);
        recycler.addItemDecoration(divider);
        recycler.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter();
        recycler.setAdapter(myAdapter);

        //获取数据
        requestGetGiftItem();
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private void requestGetGiftItem() {
        mApi.getGiftItem()
                .compose(this.<NetResult<List<BGetGiftItem>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetGiftItem>>>() {
                    @Override
                    public void accept(NetResult<List<BGetGiftItem>> listNetResult) throws Exception {
                        if(listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(),listNetResult.getMsg(),false);
                            Log.e("getGiftItem--" + listNetResult.getMsg());
                            return;
                        }
                        mDataList.clear();
                        for(int i = 0 ; i < listNetResult.getData().size() ; i++) {
                            if(listNetResult.getData().get(i).getIs_show() == 1) {
                                //显示
                                mDataList.add(listNetResult.getData().get(i));
                            }
                        }
                        myAdapter.notifyDataSetChanged();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(),throwable.toString(),false);
                        Log.e("getGiftItem--" + throwable.toString());
                    }
                });
    }

    /**
     * 用钻石购买礼物
     * @param item
     */
    private void requestBuyGift(final BGetGiftItem item) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("giftId",item.getGift_id());
        params.put("coin",item.getCoin());
        params.put("handleType",1); //充值到我的礼物中
        mApi.buyGiftByCoin(params)
                .compose(this.<NetResult>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult>() {
                    @Override
                    public void accept(NetResult netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), netResult.getMsg(), false);
                            Log.e("requestBuyGift--" + netResult.getMsg());
                            return;
                        }

                        //更新我的礼物
                        Tools.toast(getApplicationContext(), "购买" + item.getName() + "成功", false);
                        EventBusParam param = new EventBusParam();
                        param.setEventBusCode(EventBusParam.EVENT_BUS_GIFT_BUY);
                        EventBus.getDefault().post(param);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(), throwable.toString(), false);
                        Log.e("requestGetGiftItem--" + throwable.toString());
                    }
                });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textDescription;
        public ImageView imageView;
        public View viewBuy;
        public TextView textCoin;
        public TextView textName;

        public MyViewHolder(View itemView) {
            super(itemView);

            textDescription = itemView.findViewById(R.id.text_description);
            imageView = itemView.findViewById(R.id.image);
            viewBuy = itemView.findViewById(R.id.linearLayout_buy);
            textCoin = itemView.findViewById(R.id.text_coin);
            textName = itemView.findViewById(R.id.text_name);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyGiftBuyActivity.this)
                    .inflate(R.layout.layout_gift_buy_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final BGetGiftItem bean = mDataList.get(position);
            Glide.with(MyGiftBuyActivity.this)
                    .load(bean.getImage())
                    .into(holder.imageView);
            holder.textCoin.setText(String.valueOf(bean.getCoin()));
            holder.textDescription.setText(bean.getDescription());
            holder.textName.setText(bean.getName());
            holder.viewBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //购买礼物
                    requestBuyGift(bean);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
