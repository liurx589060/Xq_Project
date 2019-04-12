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
import com.cd.xq.beans.BGetChatRoomList;
import com.cd.xq.beans.BGetConsumeHistory;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 足迹（历史记录）
 * Created by Administrator on 2019/3/16.
 */

public class MyFootprinterActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private XqRequestApi mApi;
    private ArrayList<BGetChatRoomList> mDataList;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_footprinter);
        ButterKnife.bind(this);
        mApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);

        init();
    }

    private void init() {
        mDataList = new ArrayList<>();

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
        myAdapter = new MyAdapter();
        recycler.setAdapter(myAdapter);

        //获取我参与的房间
        requestGetChatRoomListByUser();
    }

    /**
     * 获取消费记录
     */
    private void requestGetChatRoomListByUser() {
        //获取未消耗的礼物
        mApi.getChatRoomListByUser(DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<NetResult<List<BGetChatRoomList>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<List<BGetChatRoomList>>>() {
                    @Override
                    public void accept(NetResult<List<BGetChatRoomList>> listNetResult) throws
                            Exception {
                        if (listNetResult.getStatus() != XqErrorCode.SUCCESS) {
                            Tools.toast(getApplicationContext(), listNetResult.getMsg(), false);
                            Log.e("requestGetChatRoomListByUser--" + listNetResult.getMsg());
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
                        Log.e("requestGetChatRoomListByUser--" + throwable.toString());
                    }
                });
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
            BGetChatRoomList bean = mDataList.get(position);
            if(bean == null) return;
            holder.textCreator.setText(bean.getCreater());
            String exitTime = "";
            if(bean.getExit_time() != null) {
                exitTime = bean.getExit_time();
            }else {
                try{
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
                    calendar.setTime(sdf.parse(bean.getEnter_time()));
                    calendar.set(Calendar.HOUR,2); //往后的两小时
                    exitTime = sdf.format(calendar.getTime());
                }catch (Exception e) {
                    Log.e("MyFootprinterActivity--" + e.toString());
                }
            }
            holder.textEndTime.setText(exitTime);
            holder.textStartTime.setText(bean.getEnter_time());
            holder.textRoomId.setText(String.valueOf(bean.getRoom_id()));
            holder.textStatus.setText(bean.getStatus()==1?"成功":"失败");
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }
}
