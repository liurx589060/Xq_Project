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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.R;
import com.cd.xq.beans.BGetConsumeHistory;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.common.MultiItemDivider;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.DateUtils;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 消费记录
 * Created by Administrator on 2019/3/16.
 */

public class MyConsumeHistoryActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.text_statistics)
    TextView textStatistics;

    private XqRequestApi mApi;
    private ArrayList<BGetConsumeHistory> mDataList;
    private MyAdapter myAdapter;
    private int mIntCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consume_history);
        ButterKnife.bind(this);
        mApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);

        init();
    }

    private void init() {
        mDataList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL,
                false);
        recycler.setLayoutManager(layoutManager);
        MultiItemDivider divider = new MultiItemDivider(this, DividerItemDecoration
                .VERTICAL,
                ContextCompat.getDrawable(this, R.drawable.shape_consume_history_recycler_divider));
        divider.setDividerMode(MultiItemDivider.INSIDE);
        recycler.addItemDecoration(divider);
        myAdapter = new MyAdapter();
        recycler.setAdapter(myAdapter);

        //获取消费记录
        requestGetConsumeHistory();
    }

    /**
     * 获取消费记录
     */
    private void requestGetConsumeHistory() {
        //获取未消耗的礼物
        mApi.getConsumeHistory(DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<NetResult<List<BGetConsumeHistory>>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetConsumeHistory>>>() {
                    @Override
                    public void accept(NetResult<List<BGetConsumeHistory>> listNetResult) throws
                            Exception {
                        if (listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), listNetResult.getMsg(), false);
                            Log.e("requestGetConsumeHistory--" + listNetResult.getMsg());
                            return;
                        }

                        if(listNetResult.getData() == null) return;
                        mDataList.clear();
                        mIntCount = 0;
                        mDataList.addAll(listNetResult.getData());
                        myAdapter.notifyDataSetChanged();
                        for(int i = 0 ; i < mDataList.size() ; i++) {
                            mIntCount += mDataList.get(i).getCoin()*mDataList.get(i).getNum();
                        }
                        textStatistics.setText("你共消费" + mIntCount + "钻石");

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(), throwable.toString(), false);
                        Log.e("requestGetConsumeHistory--" + throwable.toString());
                    }
                });
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textContent;
        public TextView textTime;
        public ImageView imageHead;
        public ImageView imageGift;

        public MyViewHolder(View itemView) {
            super(itemView);

            textContent = itemView.findViewById(R.id.text_content);
            textTime = itemView.findViewById(R.id.text_time);
            imageGift = itemView.findViewById(R.id.image_gift);
            imageHead = itemView.findViewById(R.id.image_head);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyConsumeHistoryActivity.this)
                    .inflate(R.layout.layout_consume_history_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            BGetConsumeHistory bean = mDataList.get(position);
            String str = "";
            if (bean.getType() == Constant.GIFT_TYPE_LIPIN) {
                if(bean.getTo_user().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                    str = "你购买了" + bean.getNum() + "个" + bean.getName();
                }else {
                    str = "你送" + bean.getNick_name() + bean.getNum() + "个" + bean.getName();
                }
            } else if (bean.getType() == Constant.GIFT_TYPE_CARD) {
                str = "你购买了" + bean.getNum() + "张" + bean.getName();
            } else if(bean.getType() == Constant.GIFT_TYPE_FIX) {
                str = "你消费了" + bean.getCoin() + "钻石用于" + bean.getName();
            }
            holder.textContent.setText(str);
            holder.textTime.setText(bean.getCreate_time());
            Glide.with(MyConsumeHistoryActivity.this)
                    .load(bean.getImage())
                    .into(holder.imageGift);
            if(!bean.getTo_user().equals(DataManager.getInstance().getUserInfo().getUser_name())) {
                holder.imageHead.setVisibility(View.VISIBLE);
                Glide.with(MyConsumeHistoryActivity.this)
                        .load(bean.getHead_image())
                        .transform(new GlideCircleTransform(MyConsumeHistoryActivity.this))
                        .into(holder.imageHead);
            }else {
                holder.imageHead.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
