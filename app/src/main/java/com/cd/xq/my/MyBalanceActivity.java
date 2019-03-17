package com.cd.xq.my;

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

import com.cd.xq.R;
import com.cd.xq.module.util.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_balance);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        LinearLayoutManager layoutManager = new  LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false);
        recyclerViewGoods.setLayoutManager(layoutManager);
        recyclerViewGoods.setNestedScrollingEnabled(false);
        recyclerViewGoods.setAdapter(new MyAdapter());
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

        }

        @Override
        public int getItemCount() {
            return 6;
        }
    }
}
