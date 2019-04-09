package com.cd.xq.module.chart.manager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cd.xq.module.util.glide.GlideCircleTransform;
import com.cd.xq_chart.module.R;

/**
 * Created by Administrator on 2018/12/1.
 */

public class HeadInfoViewMg {
    private Activity mActivity;
    private View mRootView;

    public TextView mTextNickName;
    public ImageView mImgHead;
    public TextView mTextSpecialInfo;
    public TextView mTextContent;
    public Button mBtnReport;
    public Button mBtnGift;

    public HeadInfoViewMg(Activity activity,View view) {
        mActivity = activity;
        if(view == null) {
            mRootView = LayoutInflater.from(mActivity).inflate(R.layout.layout_head_info,null);
        }else {
            mRootView = view;
        }
        init();
    }

    private void init() {
        mTextContent = mRootView.findViewById(R.id.head_info_text_content);
        if(mTextContent == null) {
            throw new IllegalArgumentException("请使用layout_head_info.xml");
        }
        mImgHead = mRootView.findViewById(R.id.head_info_image_head);
        mTextNickName = mRootView.findViewById(R.id.head_info_text_nickName);
        mTextSpecialInfo = mRootView.findViewById(R.id.head_info_text_specialInfo);
        mBtnReport = mRootView.findViewById(R.id.head_info_btn_report);
        mBtnGift = mRootView.findViewById(R.id.head_info_btn_gift);
        mBtnReport.setVisibility(View.GONE);
    }

    public View getView() {
        return mRootView;
    }

    public void setNickName(String nickName) {
        mTextNickName.setText(nickName);
    }

    public void setImgHead(String imgPath) {
        Glide.with(mActivity)
                .load(imgPath)
                .placeholder(R.drawable.chart_room_default_head)
                .bitmapTransform(new GlideCircleTransform(mActivity))
                .into(mImgHead);
    }

    public void setSpecialInfo(String info) {
        mTextSpecialInfo.setText(info);
    }

    public void setContent(String content) {
        mTextContent.setText(content);
    }

    public void setSpecailInfoVisible(boolean isVisible) {
        mTextSpecialInfo.setVisibility(isVisible?View.VISIBLE:View.GONE);
    }

    public void setVisible(boolean isVisible) {
        mRootView.setVisibility(isVisible?View.VISIBLE:View.GONE);
    }
}
