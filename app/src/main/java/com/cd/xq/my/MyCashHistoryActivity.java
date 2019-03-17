package com.cd.xq.my;

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
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.common.MultiItemDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提现记录
 * Created by Administrator on 2019/3/17.
 */

public class MyCashHistoryActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cash_history);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        LinearLayoutManager layoutManager = new  LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false);
        recycler.setLayoutManager(layoutManager);
        MultiItemDivider divider = new MultiItemDivider(this, DividerItemDecoration
                .VERTICAL,
                ContextCompat.getDrawable(this, R.drawable.shape_consume_history_recycler_divider));
        divider.setDividerMode(MultiItemDivider.INSIDE);
        recycler.addItemDecoration(divider);
        recycler.setAdapter(new MyAdapter());
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textContent;
        public TextView textTime;

        public MyViewHolder(View itemView) {
            super(itemView);

            textContent = itemView.findViewById(R.id.text_content);
            textTime = itemView.findViewById(R.id.text_time);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyCashHistoryActivity.this)
                    .inflate(R.layout.layout_cash_history_recycler_item,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String text = "您提现了 " + position + "元";
            holder.textContent.setText(text);
        }

        @Override
        public int getItemCount() {
            return 200;
        }
    }
}
