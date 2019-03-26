package com.cd.xq.module.chart.manager;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.xq.module.chart.fragment.InnerGiftFragment;
import com.cd.xq.module.chart.fragment.RecommendFragment;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq_chart.module.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 赠送礼物的View Manager
 * Created by Administrator on 2019/3/24.
 */

public class PresentGiftViewMg{
    private View mRootView;
    private AppCompatActivity mActivity;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Button mBtnCharge;
    private TextView mTextBalance;
    private PresentPayViewMg mPayViewMg;
    private Animation mAnimationShow;
    private Animation mAnimationHide;
    private ArrayList<InnerGiftFragment> mFragmentList;
    private MyFragmentAdapter mViewPagerAdapter;
    private IPresentGiftMg mListener;

    public void setListener(IPresentGiftMg mListener) {
        this.mListener = mListener;
    }


    public interface IPresentGiftMg {
        void onGiftShowed();
        void onGiftHid();
    }

    public PresentGiftViewMg(AppCompatActivity activity) {
        mActivity = activity;
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_present_gift,null);
        EventBus.getDefault().register(this);

        mViewPager = mRootView.findViewById(R.id.viewPager_gift);
        mTabLayout = mRootView.findViewById(R.id.tabLayout_gift);
        mBtnCharge = mRootView.findViewById(R.id.btn_gift_charge);
        mTextBalance = mRootView.findViewById(R.id.text_gift_balance);

        init();
    }

    private void init() {
        mFragmentList = new ArrayList<>();
        {
            RecommendFragment fragment = new RecommendFragment();
            mFragmentList.add(fragment);
        }

        mAnimationShow = AnimationUtils.loadAnimation(mActivity,R.anim.translate_present_gift_show);
        mAnimationHide = AnimationUtils.loadAnimation(mActivity,R.anim.translate_present_gift_hide);
        mAnimationHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mListener != null) {
                    mListener.onGiftHid();
                }
                mRootView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnimationShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mListener != null) {
                    mListener.onGiftShowed();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBtnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPayViewMg == null) {
                    mPayViewMg = new PresentPayViewMg(mActivity);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                            .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    ((RelativeLayout)mRootView).addView(mPayViewMg.getRootView(),params);
                }
                mPayViewMg.show();
            }
        });

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootView.startAnimation(mAnimationHide);
            }
        });

        initTabLayout();
        setBalance();
    }

    private void setBalance() {
        mTextBalance.setText("余额：" + String.valueOf(DataManager.getInstance().getUserInfo().getBalance()));
    }

    private void initTabLayout() {
        mViewPagerAdapter = new MyFragmentAdapter(mActivity.getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void show() {
        mRootView.setVisibility(View.VISIBLE);
        mRootView.startAnimation(mAnimationShow);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusParam param) {
        if(param.getEventBusCode() == EventBusParam.EVENT_BUS_PAY_SUCCESS) {
            //支付成功
            setBalance();
            if(mPayViewMg != null) {
                mPayViewMg.setBalance();
            }
        }
    }

    public View getRootView() {
        return mRootView;
    }

    private class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).getTitle();
        }
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(mPayViewMg != null) {
            mPayViewMg.onDestroy();
        }
    }
}
