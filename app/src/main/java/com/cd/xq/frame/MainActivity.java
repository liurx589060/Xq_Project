package com.cd.xq.frame;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.cd.xq.AppService;
import com.cd.xq.R;
import com.cd.xq.beans.BCheckUpdate;
import com.cd.xq.login.BlackCheckListener;
import com.cd.xq.login.RegisterInfoActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.base.BaseFragment;
import com.cd.xq.module.util.base.SlideViewPager;
import com.cd.xq.module.util.beans.EventBusParam;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.beans.user.UserResp;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetParse;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.module.util.network.RequestApi;
import com.cd.xq.module.util.tools.IDCardUtils;
import com.cd.xq.module.util.tools.Log;
import com.cd.xq.module.util.tools.Tools;
import com.cd.xq.module.util.tools.XqErrorCode;
import com.cd.xq.network.XqRequestApi;
import com.cd.xq.utils.AppTools;
import com.cd.xq.utils.CheckUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

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
    private XqRequestApi mXqApi;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mHandler = new Handler();
        mXqApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);
        //启动AppService
        Intent intent = new Intent(this, AppService.class);
        startService(intent);

        mApi = NetWorkMg.newRetrofit().create(RequestApi.class);
        UserInfo userInfo = JMessageClient.getMyInfo();
        if(userInfo != null && (!DataManager.getInstance().getUserInfo().isOnLine())) {
            //自动登陆
            DataManager.getInstance().setJmUserName(userInfo.getUserName());
            toAutoLogin();
        }

        init();

        //检测是否更新
        requestCheckUpdate();
    }

    private void onLogin() {
        for(int i = 0 ; i < mFragmentHolderList.size() ; i++) {
            if(mFragmentHolderList.get(i).mFragment != null) {
                mFragmentHolderList.get(i).mFragment.onLogin();
            }
        }

        if(DataManager.getInstance().getUserInfo().getCard_valid() == 0) {
            //没实名制
            showIDCardSupply();
        }
    }

    /**
     * 检查更新
     */
    private void requestCheckUpdate() {
        mXqApi.checkUpdate(Tools.getVersionCode(this))
                .compose(this.<NetResult<BCheckUpdate>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BCheckUpdate>>() {
                    @Override
                    public void accept(NetResult<BCheckUpdate> bCheckUpdateNetResult) throws Exception {
                        BCheckUpdate bean = bCheckUpdateNetResult.getData();
                        if(bean == null) return;
                        UIData uiData = UIData.create();
                        uiData.setTitle("检测到新版本");
                        String content = bean.getMessage().replace("\\n","\n");
                        uiData.setContent(content);
                        uiData.setDownloadUrl(bean.getDown_url());
                        DownloadBuilder builder=AllenVersionChecker
                                .getInstance()
                                .downloadOnly(uiData);
                        builder.setNewestVersionCode(bean.getVersion_code());
                        if(bean.getIs_force() == 1) {
                            //强制更新
                            builder.setForceUpdateListener(new ForceUpdateListener() {
                                @Override
                                public void onShouldForceUpdate() {
                                }
                            });
                        }
                        builder.executeMission(MainActivity.this);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("requestCheckUpdate--" + throwable.toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestCheckUpdate();
                            }
                        },5000);
                    }
                });
    }


    private void toAutoLogin() {
        SharedPreferences sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        final String userName = sp.getString("userName","");
        final String password = sp.getString("password","");
        Tools.checkUserOrBlack(this, userName,new BlackCheckListener() {
            @Override
            public void onResult(boolean isBlack) {
                if(!isBlack) {
                    autoLogin(userName,password);
                }
            }
        });
    }

    /**
     * 自动登陆自己的服务器
     */
    @SuppressLint("CheckResult")
    private void autoLogin(String userName,String password) {
        mApi.login(userName,password)
                .subscribeOn(Schedulers.io())
                .compose(this.<UserResp>bindUntilEvent(ActivityEvent.DESTROY))
                .retry(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserResp>() {
                    @Override
                    public void accept(UserResp userResp) throws Exception {
                        if(userResp.getStatus() == XqErrorCode.SUCCESS) {
                            DataManager.getInstance().setUserInfo(userResp.getData());
                            DataManager.getInstance().getUserInfo().setOnLine(true);
                            onLogin();

                            //检测信息是否完整
                            if (CheckUtil.checkToCompleteUserInfo(DataManager.getInstance().getUserInfo())) {
                                Intent intent = new Intent(MainActivity.this, RegisterInfoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("from", RegisterInfoActivity.FROM_LEAK_INFO);
                                intent.putExtras(bundle);
                                startActivityForResult(intent,2);
                            }

                            //通知获取好友列表
                            EventBusParam param = new EventBusParam();
                            param.setEventBusCode(EventBusParam.EVENT_BUS_GET_FRIENDLIST);
                            EventBus.getDefault().post(param);

                            //通知好友上线
                            AppTools.notifyFriendOnLine(true);

                        }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_PASSWORD_WRONG) {
                            Tools.toast(getApplicationContext(),"密码错误",true);
                        }else if(userResp.getStatus() == XqErrorCode.ERROR_USER_NOT_EXIST) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AllenVersionChecker.getInstance().cancelAllMission(this);
        DataManager.getInstance().getUserInfo().setOnLine(false);
        Intent intent = new Intent(this, AppService.class);
        stopService(intent);

        //告知好友离线
        AppTools.notifyFriendOnLine(false);
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

    /**
     * 补充实名制
     */
    private void showIDCardSupply() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_idcard_supply,null);
        final EditText editName = contentView.findViewById(R.id.edit_name);
        final EditText editIdCard = contentView.findViewById(R.id.edit_idcard);
        Button btnIngore = contentView.findViewById(R.id.btn_ignore);
        Button btnCommit = contentView.findViewById(R.id.btn_commit);
        editIdCard.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return android.text.InputType.TYPE_CLASS_PHONE;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'X' };
                return numberChars;
            }
        });
        btnIngore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String idCard = editIdCard.getText().toString();
                if(TextUtils.isEmpty(name)
                        || TextUtils.isEmpty(idCard)) {
                    Tools.toast(getApplicationContext(),"请完善资料",false);
                    return;
                }
                if(!Tools.isLegalName(name)) {
                    Tools.toast(getApplicationContext(),"非法的姓名",false);
                    return;
                }
                try {
                    if(!IDCardUtils.IDCardValidate(idCard)) {
                        Tools.toast(getApplicationContext(),"非法的身份证号",false);
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }

                requestCheckIDCard(dialog,name,idCard);
            }
        });
        if(DataManager.getInstance().getAppSettings().getIs_idcard_must() == 1) {
            //必须实名制，否在退出程序
            btnIngore.setVisibility(View.GONE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0) {
                        return true;
                    }
                    return false;
                }
            });
        }

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setContentView(contentView);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_300);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
    }

    private void requestCheckIDCard(final Dialog dialog, String name, String idCard) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userName",DataManager.getInstance().getUserInfo().getUser_name());
        params.put("name",name);
        params.put("idCard",idCard);
        params.put("handleType",2);
        mApi.checkIDCard(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult>() {
                    @Override
                    public void accept(NetResult netResult) throws Exception {
                        if(!NetParse.parseNetResult(getApplicationContext(),netResult)) return;
                        Tools.toast(getApplicationContext(),"实名认证成功",false);
                        dialog.dismiss();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        NetParse.parseError(getApplicationContext(),throwable);
                    }
                });
    }
}
