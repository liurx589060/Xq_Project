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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cd.xq.module.chart.beans.BGetGiftItem;
import com.cd.xq.module.chart.fragment.CardFragment;
import com.cd.xq.module.chart.fragment.InnerGiftFragment;
import com.cd.xq.module.chart.fragment.PackageFragment;
import com.cd.xq.module.chart.fragment.RecommendFragment;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.jmessage.Member;
import com.cd.xq.module.util.beans.jmessage.UserInfo;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.tools.Log;
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
    private TextView mGiftDescription;
    private TextView mTargetUser;
    private PresentPayViewMg mPayViewMg;
    private Animation mAnimationShow;
    private Animation mAnimationHide;
    private ArrayList<InnerGiftFragment> mFragmentList;
    private MyFragmentAdapter mViewPagerAdapter;
    private IPresentGiftMg mListener;
    private ImageView mGifImageView;
    private boolean mIsAnimate;
    private Member mTargetMember;

    public void setListener(IPresentGiftMg mListener) {
        this.mListener = mListener;
    }

    public interface IPresentGiftMg {
        void onGiftShowed();
        void onGiftHid();
        void onConsume(Member member,BGetGiftItem item);
    }

    public PresentGiftViewMg(AppCompatActivity activity) {
        mActivity = activity;
        mRootView = LayoutInflater.from(activity).inflate(R.layout.layout_present_gift,null);
        EventBus.getDefault().register(this);

        mViewPager = mRootView.findViewById(R.id.viewPager_gift);
        mTabLayout = mRootView.findViewById(R.id.tabLayout_gift);
        mBtnCharge = mRootView.findViewById(R.id.btn_gift_charge);
        mTextBalance = mRootView.findViewById(R.id.text_gift_balance);
        mGifImageView = mRootView.findViewById(R.id.image_gif);
        mGiftDescription = mRootView.findViewById(R.id.text_gift_description);
        mTargetUser = mRootView.findViewById(R.id.text_target_user);

        mRootView.setVisibility(View.GONE); //开始隐藏
        init();
    }

    public void setTargetUser(Member member) {
        mTargetMember = member;
        String str = "";
        if(member.getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            str = "赠送目标：爱心大使";
        }else if(member.getUserInfo().getRole_type().equals(Constant.ROLETYPE_GUEST)){
            if(member.getUserInfo().getGender().equals(Constant.GENDER_MAN)) {
                str = "赠送目标：男嘉宾";
            }else if(member.getUserInfo().getGender().equals(Constant.GENDER_LADY)) {
                str = "赠送目标：" + member.getIndex() + "号女嘉宾";
            }
        }
        mTargetUser.setText(str);
    }

    /**
     * 选中礼物后的处理
     * @param giftItem
     */
    public void setGiftSelectedData(BGetGiftItem giftItem) {
        mGiftDescription.setText(giftItem.getDescription());
    }

    public void setGiftConsume(BGetGiftItem item) {
        if(mListener != null) {
            mListener.onConsume(mTargetMember,item);
        }
    }

    private void init() {
        mFragmentList = new ArrayList<>();
        {
            RecommendFragment fragment = new RecommendFragment();
            mFragmentList.add(fragment);
        }

        {
            CardFragment fragment = new CardFragment();
            mFragmentList.add(fragment);
        }

        {
            PackageFragment fragment = new PackageFragment();
            mFragmentList.add(fragment);
        }

        for(int i = 0 ; i < mFragmentList.size() ; i++) {
            mFragmentList.get(i).setGiftViewMg(this);
        }

        mAnimationShow = AnimationUtils.loadAnimation(mActivity,R.anim.translate_present_gift_show);
        mAnimationHide = AnimationUtils.loadAnimation(mActivity,R.anim.translate_present_gift_hide);
        mAnimationHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mListener != null) {
                    mListener.onGiftHid();
                }
                mRootView.setVisibility(View.GONE);
                mRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsAnimate = false;
                    }
                },100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnimationShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mListener != null) {
                    mListener.onGiftShowed();
                }
                mRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsAnimate = false;
                    }
                },100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBtnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPayView();
            }
        });

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsAnimate) return;
                mRootView.startAnimation(mAnimationHide);
            }
        });

        initTabLayout();
        initPayView();
        initGifView();
        setBalance();
    }

    private void initPayView() {
        mPayViewMg = new PresentPayViewMg(mActivity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ((RelativeLayout)mRootView).addView(mPayViewMg.getRootView(),params);
    }

    private void initGifView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_present_gif_view,null);
        mGifImageView = view.findViewById(R.id.image_gif);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mActivity.addContentView(view,params);
    }

    private void showPayView() {
        mPayViewMg.show();
    }

    private void setBalance() {
        mTextBalance.setText("余额：" + String.valueOf(DataManager.getInstance().getUserInfo().getBalance()));
    }

    private void initTabLayout() {
        mViewPagerAdapter = new MyFragmentAdapter(mActivity.getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager,false);
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

    public Member getTargetUser() {
        return mTargetMember;
    }

    /**
     * 余额不足，跳转到充值界面
     */
    public void lackShowPayView() {
        showPayView();
    }

    /**
     * 消费礼物成功，播放gif
     * @param giftItem
     */
    public void playGiftGifImage(BGetGiftItem giftItem) {
        setBalance();
        if(mPayViewMg != null) {
            mPayViewMg.setBalance();
        }
        //播放gif
        mGifImageView.setVisibility(View.VISIBLE);
        Glide.with(mActivity)
                .load(giftItem.getGif())
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        // 计算动画时长
                        int duration = 0;
                        GifDrawable drawable = resource;
                        GifDecoder decoder = drawable.getDecoder();
                        for (int i = 0; i < drawable.getFrameCount(); i++) {
                            duration += decoder.getDelay(i);
                        }
                        mGifImageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mGifImageView.setVisibility(View.GONE);
                            }
                        },2*duration);
                        return false;
                    }
                })
                .into(mGifImageView);
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
