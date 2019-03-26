package com.cd.xq.module.chart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq_chart.module.R;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2019/3/24.
 */

public class RecommendFragment extends InnerGiftFragment {
    private RecyclerView mRecyclerView;
    private ArrayList<BGetGiftItem> mDataList;
    private MyAdapter myAdapter;
    private int mClickedIndex = -1;
    private ChatRequestApi mApi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_gift_recommend, null);
        mApi = NetWorkMg.newRetrofit().create(ChatRequestApi.class);

        init();

        return mRootView;
    }

    private void init() {
        mDataList = new ArrayList<>();
        myAdapter = new MyAdapter();

        mRecyclerView = mRootView.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2,
                GridLayoutManager.HORIZONTAL,false));
        mRecyclerView.setAdapter(myAdapter);

        //获取支付项
        requestGetGiftItem();
    }

    @Override
    public String getTitle() {
        return "推荐";
    }

    public void refresh(ArrayList<BGetGiftItem> list) {
        mDataList.clear();
        mDataList.addAll(list);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 获取礼物Item
     */
    private void requestGetGiftItem() {
        mApi.getGiftItem()
                .compose(this.<NetResult<List<BGetGiftItem>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetGiftItem>>>() {
                    @Override
                    public void accept(NetResult<List<BGetGiftItem>> listNetResult) throws Exception {
                        if(listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getActivity().getApplicationContext(),listNetResult.getMsg(),false);
                            Log.e("requestGetGiftItem--" + listNetResult.getMsg());
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
                        Tools.toast(getActivity().getApplicationContext(),throwable.toString(),false);
                        Log.e("getGiftItem--" + throwable.toString());
                    }
                });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView textCoin;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            textCoin = itemView.findViewById(R.id.text_coin);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getActivity())
                    .inflate(R.layout.layout_recycler_gift_gift_item,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            BGetGiftItem bean = mDataList.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickedIndex = position;
                    notifyDataSetChanged();
                }
            });

            if(position == mClickedIndex) {
                holder.itemView.setBackgroundResource(R.drawable.shape_recycler_gift_item_bg_p);
            }else {
                if((position+1)%4==0 && position > 0) {
                    holder.itemView.setBackgroundResource(R.drawable.shape_recycler_gift_item_bg_right);
                }else {
                    holder.itemView.setBackgroundResource(R.drawable.shape_recycler_gift_item_bg_normal);
                }
            }

            holder.textCoin.setText(String.valueOf(bean.getCoin()));
            Glide.with(getActivity())
                    .load(bean.getImage())
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
