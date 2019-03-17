package com.cd.xq.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.R;
import com.cd.xq.module.util.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 礼券
 * Created by Administrator on 2019/3/16.
 */

public class MyCertificateActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.recyclerView_gift)
    RecyclerView recyclerViewGift;
    @BindView(R.id.recyclerView_certificate)
    RecyclerView recyclerViewCertificate;

    private MyAdapter mGiftAdapter;
    private MyAdapter mCertificateAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_certificate);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        recyclerViewGift.setLayoutManager(new GridLayoutManager(this,3));
        mGiftAdapter = new MyAdapter();
        recyclerViewGift.setAdapter(mGiftAdapter);

        mCertificateAdapter = new MyAdapter();
        recyclerViewCertificate.setLayoutManager(new GridLayoutManager(this,3));
        recyclerViewCertificate.setAdapter(mCertificateAdapter);
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public TextView textCount;
        public ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_name);
            textCount = itemView.findViewById(R.id.text_count);
            image = itemView.findViewById(R.id.image);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyCertificateActivity.this)
                    .inflate(R.layout.layout_certificate_recycler_item,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textCount.setText("X " + position);
            Glide.with(MyCertificateActivity.this)
                    .load(R.drawable.card_liping)
                    .into(holder.image);
            holder.textName.setText("建房卡");
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}
