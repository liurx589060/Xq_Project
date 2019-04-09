package com.cd.xq.module.chart.fragment;

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
import com.cd.xq.module.chart.beans.BConsumeGift;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq_chart.module.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2019/3/24.
 */

public class PackageFragment extends InnerGiftFragment {
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

        //获取我的礼物
        requestGetGiftList();
    }

    @Override
    public String getTitle() {
        return "包裹";
    }

    public void refresh(ArrayList<BGetGiftItem> list) {
        mDataList.clear();
        mDataList.addAll(list);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 获取我的礼物列表
     */
    private void requestGetGiftList() {
        //获取未消耗的礼物
        mApi.getGiftList(DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<NetResult<List<BGetGiftItem>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetGiftItem>>>() {
                    @Override
                    public void accept(NetResult<List<BGetGiftItem>> listNetResult) throws
                            Exception {
                        if (listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getActivity().getApplicationContext(), listNetResult.getMsg(), false);
                            Log.e("requestGetGiftList--" + listNetResult.getMsg());
                            return;
                        }
                        mDataList.clear();
                        for (int i = 0; i < listNetResult.getData().size(); i++) {
                            if(listNetResult.getData().get(i).getGift_id() != Constant.GIFT_ID_JIANFANG) {
                                mDataList.add(listNetResult.getData().get(i));
                            }
                        }
                        myAdapter.notifyDataSetChanged();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getActivity().getApplicationContext(), throwable.toString(), false);
                        Log.e("requestGetGiftList--" + throwable.toString());
                    }
                });
    }

    /**
     * 送礼物,包裹消费
     * @param item
     */
    private void requestConsumeGift(final BGetGiftItem item) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("giftId",item.getGift_id());
        params.put("coin",item.getCoin());
        params.put("toUser",mGiftViewMg.getTargetUser().getUserInfo().getUser_name());
        params.put("handleType",2);  //用包裹消费
        mApi.consumeGift(params)
                .compose(this.<NetResult<BConsumeGift>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BConsumeGift>>() {
                    @Override
                    public void accept(NetResult<BConsumeGift> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getActivity().getApplicationContext(), netResult.getMsg(), false);
                            Log.e("requestConsumeGift--" + netResult.getMsg());
                            return;
                        }

                        Tools.toast(getActivity().getApplicationContext(), "你送出了" + item.getName()
                                , false);
                        //更新包裹
                        mDataList.clear();
                        for (int i = 0; i < netResult.getData().getGift_list().size(); i++) {
                            if(netResult.getData().getGift_list().get(i).getGift_id() != Constant.GIFT_ID_JIANFANG
                             && netResult.getData().getGift_list().get(i).getGift_id() != Constant.GIFT_ID_RUMEN) {
                                mDataList.add(netResult.getData().getGift_list().get(i));
                            }
                        }
                        myAdapter.notifyDataSetChanged();

                        //播放gif
                        mGiftViewMg.setGiftConsume(item);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getActivity().getApplicationContext(), throwable.toString(), false);
                        Log.e("requestConsumeGift--" + throwable.toString());
                    }
                });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView textCoin;
        public View viewSend;
        public Button btnSend;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            textCoin = itemView.findViewById(R.id.text_coin);
            viewSend = itemView.findViewById(R.id.linear_send);
            btnSend = itemView.findViewById(R.id.btn_send);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getActivity())
                    .inflate(R.layout.layout_recycler_gift_package_item,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final BGetGiftItem bean = mDataList.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickedIndex = position;
                    mGiftViewMg.setGiftSelectedData(bean);
                    notifyDataSetChanged();
                }
            });

            if(position == mClickedIndex) {
                holder.viewSend.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundResource(R.drawable.shape_recycler_gift_item_bg_p);
            }else {
                holder.viewSend.setVisibility(View.GONE);
                holder.itemView.setBackgroundResource(R.drawable.shape_recycler_gift_item_bg);
            }

            holder.textCoin.setText("X " + String.valueOf(bean.getNum()));
            Glide.with(getActivity())
                    .load(bean.getImage())
                    .into(holder.image);
            holder.btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //从包裹消费礼物
                    requestConsumeGift(bean);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
