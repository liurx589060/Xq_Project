package com.cd.xq.my;

import android.graphics.Rect;
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
import com.cd.xq.module.util.tools.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 足迹（历史记录）
 * Created by Administrator on 2019/3/16.
 */

public class MyFootprinterActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_footprinter);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        LinearLayoutManager layoutManager = new  LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false);
        recycler.setLayoutManager(layoutManager);
        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                    .State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int index = parent.getChildAdapterPosition(view);
                int count = parent.getAdapter().getItemCount();
                if(index == count - 1) {
                    outRect.bottom = getResources().getDimensionPixelOffset(R.dimen.dp_10);
                }else {
                    outRect.bottom = 0;
                }
            }
        });
        recycler.setAdapter(new MyAdapter());
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textRoomId;
        public TextView textCreator;
        public TextView textStatus;
        public TextView textStartTime;
        public TextView textEndTime;

        public MyViewHolder(View itemView) {
            super(itemView);

            textRoomId = itemView.findViewById(R.id.text_room_id);
            textCreator = itemView.findViewById(R.id.text_creator);
            textStatus = itemView.findViewById(R.id.text_status);
            textStartTime = itemView.findViewById(R.id.text_start_time);
            textEndTime = itemView.findViewById(R.id.text_end_time);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyFootprinterActivity.this)
                    .inflate(R.layout.layout_footerprinter_recycler_item,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}
