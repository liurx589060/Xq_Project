package com.cd.xq.my;

import android.content.Intent;
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
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.network.ChatRequestApi;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
    @BindView(R.id.text_gift_buy)
    TextView textGiftBuy;

    private MyAdapter mGiftAdapter;
    private MyAdapter mCertificateAdapter;
    private ChatRequestApi mApi;
    private ArrayList<BGetGiftItem> mGiftList;
    private ArrayList<BGetGiftItem> mCertificateList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_certificate);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mApi = NetWorkMg.newRetrofit().create(ChatRequestApi.class);

        init();
    }

    private void init() {
        mGiftList = new ArrayList<>();
        mCertificateList = new ArrayList<>();

        recyclerViewGift.setLayoutManager(new GridLayoutManager(this, 3));
        mGiftAdapter = new MyAdapter(Constant.GIFT_TYPE_LIPIN);
        recyclerViewGift.setAdapter(mGiftAdapter);

        mCertificateAdapter = new MyAdapter(Constant.GIFT_TYPE_CARD);
        recyclerViewCertificate.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewCertificate.setAdapter(mCertificateAdapter);

        //获取数据
        requestGetGiftList();
    }

    /**
     * 获取我的礼物列表
     */
    private void requestGetGiftList() {
        //获取未消耗的礼物
        mApi.getGiftList(DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<NetResult<List<BGetGiftItem>>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetGiftItem>>>() {
                    @Override
                    public void accept(NetResult<List<BGetGiftItem>> listNetResult) throws
                            Exception {
                        if (listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), listNetResult.getMsg(), false);
                            Log.e("requestGetGiftList--" + listNetResult.getMsg());
                            return;
                        }
                        mCertificateList.clear();
                        mGiftList.clear();
                        for (int i = 0; i < listNetResult.getData().size(); i++) {
                            if (listNetResult.getData().get(i).getType() == Constant
                                    .GIFT_TYPE_CARD) {
                                mCertificateList.add(listNetResult.getData().get(i));
                            } else if (listNetResult.getData().get(i).getType() == Constant
                                    .GIFT_TYPE_LIPIN) {
                                mGiftList.add(listNetResult.getData().get(i));
                            }
                        }
                        mGiftAdapter.notifyDataSetChanged();
                        mCertificateAdapter.notifyDataSetChanged();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(), throwable.toString(), false);
                        Log.e("requestGetGiftList--" + throwable.toString());
                    }
                });
    }

    @OnClick({R.id.btn_back,R.id.text_gift_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.text_gift_buy:
            {
                Intent intent = new Intent(this,MyGiftBuyActivity.class);
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusParam param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_GIFT_BUY) {
            //更新我列表
            requestGetGiftList();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public TextView textCount;
        public TextView textExpiry;
        public ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_name);
            textCount = itemView.findViewById(R.id.text_count);
            textExpiry = itemView.findViewById(R.id.text_expiry);
            image = itemView.findViewById(R.id.image);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private int iType;

        public MyAdapter(int type) {
            iType = type;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MyCertificateActivity.this)
                    .inflate(R.layout.layout_certificate_recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            BGetGiftItem bean = null;
            if (iType == Constant.GIFT_TYPE_CARD) {
                bean = mCertificateList.get(position);
            } else if (iType == Constant.GIFT_TYPE_LIPIN) {
                bean = mGiftList.get(position);
            }
            if (bean == null) return;

            holder.textCount.setText("X " + String.valueOf(bean.getNum()));
            Glide.with(MyCertificateActivity.this)
                    .load(bean.getImage())
                    .into(holder.image);
            holder.textName.setText(bean.getName());
            if(bean.getStatus() == 0) {
                //未使用
                holder.textExpiry.setVisibility(View.INVISIBLE);
            }else if(bean.getStatus() == 1){
                //使用中
                holder.textExpiry.setVisibility(View.VISIBLE);
                if(bean.getEnd_time() == null) {
                    holder.textExpiry.setText("剩余次数：" + bean.getExpiry_num());
                }else {
                    holder.textExpiry.setText("截止至：" + bean.getEnd_time());
                }
            }
        }

        @Override
        public int getItemCount() {
            if (iType == Constant.GIFT_TYPE_CARD) {
                return mCertificateList.size();
            } else if (iType == Constant.GIFT_TYPE_LIPIN) {
                return mGiftList.size();
            }
            return 0;
        }
    }
}
