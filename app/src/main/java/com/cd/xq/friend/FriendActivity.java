package com.cd.xq.friend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.cd.xq.InviteRoomDlgActivity;
import com.cd.xq.R;
import com.cd.xq.manager.AppDataManager;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;
import com.rx.linklib.AbsLinkHandle;
import com.rx.linklib.HeadModel;
import com.rx.linklib.LinkAdapter;
import com.rx.linklib.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by Administrator on 2019/1/22.
 */

public class FriendActivity extends BaseActivity {
    @BindView(R.id.friend_main_recyclerView)
    RecyclerView friendMainRecyclerView;
    @BindView(R.id.friend_taitou_recyclerView)
    RecyclerView friendTaitouRecyclerView;
    @BindView(R.id.taitou_text)
    TextView taitouText;
    @BindView(R.id.taitou_relayout)
    RelativeLayout taitouRelayout;
    @BindView(R.id.friend_empty_layout)
    LinearLayout friendEmptyLayout;
    @BindView(R.id.friend_btn_close)
    Button friendBtnClose;

    private MainAdapter mainAdapter;
    private MyLinkHandle myLinkHandle;

    private List<UserInfoBean> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();
    }

    private void sortDataList(List<UserInfoBean> list) {
        Collections.sort(list, new Comparator<UserInfoBean>() {

            public int compareByFirstLetter(UserInfoBean str1, UserInfoBean str2, Utils.ELetterType type) {
                return Utils.getPinYinFirstLetter(str1.getNick_name(), type).compareTo(Utils
                        .getPinYinFirstLetter(str2.getNick_name(), type));
            }

            @Override
            public int compare(UserInfoBean o1, UserInfoBean o2) {
                return compareByFirstLetter(o1, o2, Utils.ELetterType.LETTER_ONLY_CHARACTER);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        mDataList = new ArrayList<>();

        mDataList.addAll(AppDataManager.getInstance().getFriendList());
        checkDataList();

        friendTaitouRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        friendTaitouRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        friendMainRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false));
        myLinkHandle = new MyLinkHandle();
        mainAdapter = new MainAdapter(this, mDataList, friendTaitouRecyclerView, myLinkHandle);
        friendMainRecyclerView.setAdapter(mainAdapter);
    }

    private void checkDataList() {
        if(mDataList.size() == 0) {
            friendEmptyLayout.setVisibility(View.VISIBLE);
        }else {
            friendEmptyLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.friend_btn_close)
    public void onViewClicked() {
        finish();
    }


    public class MyLinkHandle extends AbsLinkHandle<TaiTouViewHolder> {
        private LayoutInflater mLayoutInflater;

        @Override
        public void getOtherData(List<HeadModel> otherDataList) {
            mLayoutInflater = LayoutInflater.from(FriendActivity.this);
        }

        @Override
        public TaiTouViewHolder onOtherCreateViewHolder(ViewGroup parent, int viewType) {
            return new TaiTouViewHolder(mLayoutInflater.inflate(R.layout.layout_taitou_item,
                    parent, false));
        }

        @Override
        public void onOtherBindViewHolder(TaiTouViewHolder holder, int position, List<HeadModel>
                dateList, HeadModel currentModel) {
            holder.textView.setText((String) dateList.get(position).getData());
            if (currentModel == dateList.get(position)) {
                holder.textView.setTextColor(Color.parseColor("#ffffff"));
                holder.taiTouBgView.setVisibility(View.VISIBLE);
            } else {
                holder.textView.setTextColor(Color.parseColor("#aaaaaa"));
                holder.taiTouBgView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onItemVisibleScroll(int firstVisibleItemPosition, int lastVisibleItemPosition) {

        }

        @Override
        public void onHeadScroll(int headPosition, HeadModel currentHeadModel, int
                nextHeadTopDis, boolean hasNextHead) {
            int topLayoutHeight = taitouRelayout.getMeasuredHeight();
            taitouText.setText((String) currentHeadModel.getData());
            if (!hasNextHead) {
                taitouRelayout.setY(0);
                return;
            }

            if (nextHeadTopDis < topLayoutHeight) {
                taitouRelayout.setY(-(topLayoutHeight - nextHeadTopDis));
            } else {
                taitouRelayout.setY(0);
            }
        }
    }

    private class MainNormalViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textOnlineStatus;
        public View divide;
        public Button btnVideo;

        public MainNormalViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.normal_image);
            textView = itemView.findViewById(R.id.normal_textview);
            textOnlineStatus = itemView.findViewById(R.id.normal_text_online_status);
            divide = itemView.findViewById(R.id.normal_divide);
            btnVideo = itemView.findViewById(R.id.normal_btn_video);
        }
    }

    private class MainTaitouViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public View divide;

        public MainTaitouViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.taitou_text);
            divide = itemView.findViewById(R.id.taitou_divide);
        }
    }

    private class MainAdapter extends LinkAdapter<RecyclerView.ViewHolder> {
        private final int TAITOU_ITEM = 1;
        private final int NORMAL_ITEM = 2;

        private LayoutInflater mLayoutInflater;
        private String strPre = "";

        public MainAdapter(Activity activity, List<UserInfoBean> date, RecyclerView recyclerView,
                           AbsLinkHandle linkHandle) {
            super(activity, date, recyclerView, linkHandle);
            mLayoutInflater = LayoutInflater.from(activity);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case TAITOU_ITEM:
                    viewHolder = new MainTaitouViewHolder(mLayoutInflater.inflate(R.layout
                            .layout_friend_taitou_item, parent, false));
                    ((MainTaitouViewHolder) viewHolder).textView.setTextColor(Color.parseColor
                            ("#999999"));
                    break;
                case NORMAL_ITEM:
                    viewHolder = new MainNormalViewHolder(mLayoutInflater.inflate(R.layout
                            .layout_friend_normal_item, parent, false));
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MainTaitouViewHolder) {
                MainTaitouViewHolder viewHolder = (MainTaitouViewHolder) holder;
                HeadModel model = (HeadModel) getDataList().get(position);
                viewHolder.textView.setText((String) model.getData());
                viewHolder.divide.setVisibility(View.GONE);
            } else if (holder instanceof MainNormalViewHolder) {
                MainNormalViewHolder viewHolder = (MainNormalViewHolder) holder;
                final UserInfoBean model = (UserInfoBean) getDataList().get(position);
                viewHolder.textView.setText(model.getNick_name());
                if (getHeadPosList().contains(position + 1) || position == getItemCount() - 1) {
                    viewHolder.divide.setVisibility(View.GONE);
                } else {
                    viewHolder.divide.setVisibility(View.VISIBLE);
                }

                Glide.with(FriendActivity.this)
                        .load(model.getHead_image())
                        .placeholder(R.drawable.chart_room_default_head)
                        .into(viewHolder.imageView);

                if(model.isOnLine()) {
                    viewHolder.textOnlineStatus.setText("在线");
                    viewHolder.textOnlineStatus.setTextColor(Color.parseColor("#32b7b9"));
                    viewHolder.btnVideo.setEnabled(true);
                }else {
                    viewHolder.textOnlineStatus.setText("离线");
                    viewHolder.textOnlineStatus.setTextColor(Color.parseColor("#aaaaaa"));
                    viewHolder.btnVideo.setEnabled(false);
                }

                viewHolder.btnVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InviteRoomDlgActivity.startWithSend(FriendActivity.this,model);
                    }
                });
            }
        }

        @Override
        public Object isHeadData(Object object) {
            UserInfoBean bean = (UserInfoBean) object;
            String str = Utils.getPinYinFirstLetter(bean.getNick_name(), Utils.ELetterType
                    .LETTER_ONLY_CHARACTER);
            if (!str.equals(strPre)) {
                strPre = str;
                return str;
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if (getHeadPosList().contains(position)) {
                return TAITOU_ITEM;
            }
            return NORMAL_ITEM;
        }

        public void reset() {
            strPre = "";
        }
    }

    private class TaiTouViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public View taiTouBgView;

        public TaiTouViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.taitou_textview);
            taiTouBgView = itemView.findViewById(R.id.taitou_bg);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateList(EventBusParam param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_UPDATE_FRIENDLIST) {
            mDataList.clear();
            mDataList.addAll(AppDataManager.getInstance().getFriendList());
            checkDataList();
            mainAdapter.reset();
            mainAdapter.updateData(mDataList);
        }
    }

}
