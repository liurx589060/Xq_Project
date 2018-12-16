package com.cd.xq.frame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.login.RegisterActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.base.SlideViewPager;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.utils.CheckUtil;
import com.cd.xq.welcome.WelcomeActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/10/28.
 */

public class MainActivity extends BaseActivity {
    @BindView(R.id.main_viewPager)
    SlideViewPager mViewPager;
    @BindView(R.id.main_tabLayout)
    TabLayout mTabLayout;

    private MainFragmentPagerAdapter mAdapter;
    private ArrayList<FragmentHolder> mFragmentHolderList;

    private final String[] F_TITLE = {"主页","我的"};
    private final int[] F_NORMAL_IMAGE_ID = {R.drawable.frame_home_icon,R.drawable.frame_my_icon};
    private final int[] F_SEL_IMAGE_ID = {R.drawable.frame_home_icon_p,R.drawable.frame_my_icon_p};
    private final int F_SEL_TITLE_COLOR = Color.parseColor("#1296db");
    private final int F_NORMAL_TITLE_COLOR = Color.parseColor("#707070");

    private RequestApi mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(getIsRemote()) {
            NetWorkMg.IP_ADDRESS = Constant.CONSTANT_REMOTE_IP;
        }else {
            NetWorkMg.IP_ADDRESS = getSpIpAddress();
        }

        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        UserInfo userInfo = JMessageClient.getMyInfo();
        if(userInfo != null) {
            //自动登陆
            DataManager.getInstance().setJmUserName(userInfo.getUserName());
            autoLogin();
        }
        init();
    }

    /**
     * 自动登陆自己的服务器
     */
    @SuppressLint("CheckResult")
    private void autoLogin() {
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        final String userName = sp.getString("userName","");
        final String password = sp.getString("password","");
        mApi.login(userName,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        if(userResp.getStatus() == XqErrorCode.SUCCESS) {
                            DataManager.getInstance().setUserInfo(userResp.getData());
                            for(int i = 0 ; i < mFragmentHolderList.size() ; i++) {
                                if(mFragmentHolderList.get(i).mFragment != null) {
                                    mFragmentHolderList.get(i).mFragment.onLogin();
                                }
                            }

                            //检测信息是否完整
                            if (CheckUtil.checkToCompleteUserInfo(DataManager.getInstance().getUserInfo())) {
                                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("from", RegisterActivity.FROM_LEAK_INFO);
                                intent.putExtras(bundle);
                                startActivityForResult(intent,2);
                            }
                        }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_PASSWORD_WRONG) {
                            Tools.toast(getApplicationContext(),"密码错误",true);
                        }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_REGIST_UNEXIST) {
                            Tools.toast(getApplicationContext(),"用户不存在",true);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Tools.toast(getApplicationContext(),throwable.toString(),true);
                    }
                });
    }

    private void init() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        mFragmentHolderList = new ArrayList<>();
        for(int i = 0 ; i < F_TITLE.length ; i++) {
            FragmentHolder holder = new FragmentHolder();
            holder.mNormalImageID = F_NORMAL_IMAGE_ID[i];
            holder.mSelectedImageID = F_SEL_IMAGE_ID[i];
            holder.mTitle = F_TITLE[i];
            BaseFragment fragment = null;
            switch (i) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new MyFragment();
                    break;
            }
            holder.mFragment = fragment;
            mFragmentHolderList.add(holder);
            fragmentList.add(fragment);
        }

        mAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(),this,fragmentList);

        //mTabLayout.setupWithViewPager(mViewPager);
        addTab();
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setSlide(false);
        mViewPager.setCurrentItem(0); //默认选择主页
        setTabSetups(0);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = tab.getPosition();
                setTabSetups(index);
                mViewPager.setCurrentItem(index,false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void addTab() {
        for(int i = 0 ; i < mFragmentHolderList.size() ; i++) {
            mTabLayout.addTab(mTabLayout.newTab());
        }
    }

    private void setTabSetups(int index) {
        for(int i = 0 ; i < mTabLayout.getTabCount() ; i++) {
            if(mFragmentHolderList.size() > i) {
                FragmentHolder holder = mFragmentHolderList.get(i);
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                View view = tab.getCustomView();
                if(view == null) {
                    view = LayoutInflater.from(this).inflate(R.layout.tab_layout,null);
                    tab.setCustomView(view);
                }

                TextView textView = view.findViewById(R.id.tab_title);
                ImageView imageView = view.findViewById(R.id.tab_image);

                textView.setText(holder.mTitle);
                if(i == index) {
                    textView.setTextColor(F_SEL_TITLE_COLOR);
                    imageView.setImageResource(holder.mSelectedImageID);
                    //setStatusBarColor(((BaseFragment)holder.mFragment).getStatusColor());
                }else {
                    textView.setTextColor(F_NORMAL_TITLE_COLOR);
                    imageView.setImageResource(holder.mNormalImageID);
                }
            }
        }
    }

    private class FragmentHolder {
        public int mSelectedImageID;
        public int mNormalImageID;
        public String mTitle;
        public BaseFragment mFragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(int i = 0 ; i < mFragmentHolderList.size() ; i++) {
            if(mFragmentHolderList.get(i).mFragment != null) {
                mFragmentHolderList.get(i).mFragment.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    private String getSpIpAddress() {
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Activity.MODE_PRIVATE);
        return sp.getString("ipAddress",Constant.CONSTANT_LOCOL_IP);
    }

    private boolean getIsRemote() {
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Activity.MODE_PRIVATE);
        return sp.getBoolean("isRemote",false);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            System.exit(0);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
