package com.cd.xq.my;

import android.content.Intent;
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
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.beans.BGetPayHistory;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.common.MultiItemDivider;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.DateUtils;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 充值记录
 * Created by Administrator on 2019/3/16.
 */

public class MyChargeHistoryActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private XqRequestApi mApi;
    private ArrayList<BGetPayHistory> mDataList;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_charge_history);
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
                ContextCompat.getDrawable(this, R.drawable.shape_consume_history_recycler_divider));
        divider.setDividerMode(MultiItemDivider.INSIDE);
        recycler.addItemDecoration(divider);
        recycler.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter();
        recycler.setAdapter(myAdapter);

        //获取充值记录
        requestGetPayHistory();
    }

    private void requestGetPayHistory() {
        //获取所有成功的记录
        mApi.getPayHistory(DataManager.getInstance().getUserInfo().getUser_name(), 1)
                .compose(this.<NetResult<List<BGetPayHistory>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetPayHistory>>>() {
                    @Override
                    public void accept(NetResult<List<BGetPayHistory>> listNetResult) throws
                            Exception {
                        if (listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), listNetResult.getMsg(), false);
                            Log.e("getPayHistory--" + listNetResult.getMsg());
                            return;
                        }
                        mDataList.clear();
                        mDataList.addAll(listNetResult.getData());
                        myAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(), throwable.toString(), false);
                        Log.e("getPayHistory--" + throwable.toString());
                    }
                });
    }

    @OnClick({R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textContent;
        public TextView textTime;
        public TextView textOrderId;
        public TextView textSerialId;

        public MyViewHolder(View itemView) {
            super(itemView);

            textContent = itemView.findViewById(R.id.text_content);
            textTime = itemView.findViewById(R.id.text_time);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textSerialId = itemView.findViewById(R.id.text_serial_id);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyChargeHistoryActivity.this)
                    .inflate(R.layout.layout_charge_history_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            BGetPayHistory bean = mDataList.get(position);
            String text = "您充值了" + bean.getMoney()/1.0f + "元" + ",获得" + bean.getCoin() + "钻石";
            holder.textContent.setText(text);
            holder.textTime.setText("交易时间：" + bean.getModify_time());
            holder.textOrderId.setText("订单号：" + bean.getOrder_id());
            holder.textSerialId.setText("交易流水号：" + bean.getSerial_id());
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
