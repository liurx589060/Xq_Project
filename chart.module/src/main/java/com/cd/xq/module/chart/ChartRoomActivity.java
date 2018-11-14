package com.cd.xq.module.chart;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq_chart.module.R;

import java.lang.reflect.Member;

/**
 * Created by Administrator on 2018/11/14.
 */

public class ChartRoomActivity extends BaseActivity {
    private boolean mISViewLoaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_room);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(mISViewLoaded) {
        }
    }

    private void init() {

    }

    private class MemberItem {
        public View mRootView;
        public MemberViewHolder viewHolderLeft;
        public MemberViewHolder viewHolderRight;

        public MemberItem(Activity activity) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            mRootView = layoutInflater.inflate(R.layout.chart_room_member_item,null);
        }

        public MemberViewHolder getMemberViewHolderLeft() {
            if(viewHolderLeft == null) {
                viewHolderLeft = new MemberViewHolder();
                viewHolderLeft.imageBtnLight = mRootView.findViewById(R.id.chart_room_imgBtn_Light_left);
                viewHolderLeft.imageBtnMic = mRootView.findViewById(R.id.chart_room_imgBtn_mic_left);
                viewHolderLeft.imageHead = mRootView.findViewById(R.id.chart_room_img_head_left);
                viewHolderLeft.imageLabelLight = mRootView.findViewById(R.id.chart_room_img_label_light_left);
                viewHolderLeft.reLayoutBg = mRootView.findViewById(R.id.chart_room_relayout_left);
                viewHolderLeft.textIndex = mRootView.findViewById(R.id.chart_room_text_index_left);
            }

            return viewHolderLeft;
        }

        public MemberViewHolder getMemberViewHolderRight() {
            if(viewHolderRight == null) {
                viewHolderRight = new MemberViewHolder();
                viewHolderRight.imageBtnLight = mRootView.findViewById(R.id.chart_room_imgBtn_Light_right);
                viewHolderRight.imageBtnMic = mRootView.findViewById(R.id.chart_room_imgBtn_mic_right);
                viewHolderRight.imageHead = mRootView.findViewById(R.id.chart_room_img_head_right);
                viewHolderRight.imageLabelLight = mRootView.findViewById(R.id.chart_room_img_label_light_right);
                viewHolderRight.reLayoutBg = mRootView.findViewById(R.id.chart_room_relayout_right);
                viewHolderRight.textIndex = mRootView.findViewById(R.id.chart_room_text_index_right);
            }

            return viewHolderRight;
        }
    }

    private class MemberViewHolder {
        public ImageView imageHead;
        public ImageView imageLabelLight;
        public ImageButton imageBtnMic;
        public RelativeLayout reLayoutBg;
        public ImageButton imageBtnLight;
        public TextView textIndex;

        public void setLabelLightVisible(boolean isVisible) {
            imageLabelLight.setVisibility(isVisible?View.VISIBLE:View.GONE);
        }

        public void setTextIndex(int index) {
            textIndex.setText(String.valueOf(index));
        }

        public void setBgVisible(boolean isVisible) {
            reLayoutBg.setVisibility(isVisible?View.VISIBLE:View.GONE);
        }

        public void setMicVisible(boolean isVisible) {
            imageBtnMic.setVisibility(isVisible?View.VISIBLE:View.GONE);
        }

        public void setLightVisible(boolean isVisible) {
            imageBtnLight.setVisibility(isVisible?View.VISIBLE:View.GONE);
        }
    }
}
