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
import com.cd.xq.module.chart.utils.ChatTools;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.interfaces.IDialogListener;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq_chart.module.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2019/3/24.
 */

public class CardFragment extends InnerGiftFragment {
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
        return "卡包";
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
        mApi.getGiftItem(3)
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
                        mDataList.addAll(listNetResult.getData());
                        myAdapter.notifyDataSetChanged();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getActivity().getApplicationContext(),throwable.toString(),false);
                        Log.e("requestGetGiftItem--" + throwable.toString());
                    }
                });
    }

    private void doRequestConsumeGift(final BGetGiftItem item) {
        boolean isEnough = ChatTools.checkBalance(getActivity(), item.getCoin(), new IDialogListener() {
            @Override
            public void onConfirm() {
                //发送余额不足的通知，跳转到充值页面
                mGiftViewMg.lackShowPayView();
            }

            @Override
            public void onCancel() {

            }
        });
        if(!isEnough) return;
//        ChatTools.consumeConfirm(getActivity(), item, new IDialogListener() {
//            @Override
//            public void onConfirm() {
//                requestConsumeGift(item);
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });
        requestConsumeGift(item);
    }

    /**
     * 送礼物,直接金币送出
     * @param item
     */
    private void requestConsumeGift(final BGetGiftItem item) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName", DataManager.getInstance().getUserInfo().getUser_name());
        params.put("giftId",item.getGift_id());
        params.put("coin",item.getCoin());
        params.put("toUser",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("handleType",1);  //直接用金币消费
        mApi.consumeGift(params)
                .compose(this.<NetResult<BConsumeGift>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BConsumeGift>>() {
                    @Override
                    public void accept(NetResult<BConsumeGift> netResult) throws Exception {
                        if (netResult.getStatus() != XqErrorCode.SUCCESS) {
                            if(netResult.getStatus() == XqErrorCode.ERROR_LACK_STOCK) {
                                //余额不足
                                //更新余额
                                DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                                doRequestConsumeGift(item);
                            }else {
                                Tools.toast(getActivity().getApplicationContext(), netResult.getMsg(), false);
                            }
                            Log.e("requestConsumeGift--" + netResult.getMsg());
                            return;
                        }

                        Tools.toast(getActivity().getApplicationContext(), "你使用了" + item.getName()
                                , false);
                        //更新余额
                        DataManager.getInstance().getUserInfo().setBalance(netResult.getData().getBalance());
                        //跳转充值页面
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
                    .inflate(R.layout.layout_recycler_gift_gift_item,parent,false));
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

            holder.btnSend.setText("使用");
            holder.textCoin.setText(String.valueOf(bean.getCoin()));
            Glide.with(getActivity())
                    .load(bean.getImage())
                    .into(holder.image);
            holder.btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //使用卡
                    doRequestConsumeGift(bean);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
