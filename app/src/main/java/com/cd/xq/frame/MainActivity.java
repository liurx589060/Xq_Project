package com.cd.xq.frame;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.base.SlideViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        mFragmentHolderList = new ArrayList<>();
        for(int i = 0 ; i < F_TITLE.length ; i++) {
            FragmentHolder holder = new FragmentHolder();
            holder.mNormalImageID = F_NORMAL_IMAGE_ID[i];
            holder.mSelectedImageID = F_SEL_IMAGE_ID[i];
            holder.mTitle = F_TITLE[i];
            Fragment fragment = null;
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
                    setStatusBarColor(((BaseFragment)holder.mFragment).getStatusColor());
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
        public Fragment mFragment;
    }
}
