package com.cd.xq.my;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cd.xq.R;
import com.cd.xq.beans.BGetProfit;
import com.cd.xq.login.LoginActivity;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.base.BaseActivity;
import com.cd.xq.module.util.base.DefaultWebActivity;
import com.cd.xq.module.util.beans.NetResult;
import com.cd.xq.module.util.manager.DataManager;
import com.cd.xq.module.util.network.NetParse;
import com.cd.xq.module.util.network.NetWorkMg;
import com.cd.xq.network.XqRequestApi;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 收益
 * Created by Administrator on 2019/3/16.
 */

public class MyProfitActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.text_enable_profit)
    TextView textEnableProfit;
    @BindView(R.id.text_today_profit)
    TextView textTodayProfit;
    @BindView(R.id.text_total_profit)
    TextView textTotalProfit;
    @BindView(R.id.relayout_exchange)
    RelativeLayout relayoutExchange;
    @BindView(R.id.btn_cash_history)
    Button btnCashHistory;
    @BindView(R.id.btn_cash_protocol)
    Button btnCashProtocol;
    @BindView(R.id.btn_cash)
    Button btnCash;

    private XqRequestApi mXqApi;
    private boolean mIsCanExchange;
    private BGetProfit mBGetProfit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profit);
        ButterKnife.bind(this);
        mXqApi = NetWorkMg.newRetrofit().create(XqRequestApi.class);

        init();
    }

    private void init() {
        textTotalProfit.setText(String.valueOf(DataManager.getInstance().getUserInfo().getBalance()));
        if(DataManager.getInstance().getUserInfo().getRole_type().equals(Constant.ROLRTYPE_ANGEL)) {
            requestGetProfitByUser();
        }else {
            btnCash.setText("您不是爱心大使，不具有提现功能");
            btnCash.setEnabled(false);
        }
    }

    private void requestGetProfitByUser() {
        mXqApi.getProfitByUser(DataManager.getInstance().getUserInfo().getUser_name())
                .compose(this.<NetResult<BGetProfit>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetResult<BGetProfit>>() {
                    @Override
                    public void accept(NetResult<BGetProfit> bGetProfitNetResult) throws Exception {
                        if(!NetParse.parseNetResult(getApplicationContext(),bGetProfitNetResult)) return;
                        textTotalProfit.setText(String.valueOf(bGetProfitNetResult.getData().getBalance()));
                        mBGetProfit = bGetProfitNetResult.getData();
                        if(bGetProfitNetResult.getData().getIs_exchange() != 1) {
                            //不可提现
                            btnCash.setText("暂不可提现（点击查看原因）");
                            mIsCanExchange = false;
                        }else {
                            mIsCanExchange = true;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @OnClick({R.id.btn_back, R.id.relayout_exchange, R.id.btn_cash_history, R.id
            .btn_cash_protocol, R.id.btn_cash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.relayout_exchange:
                break;
            case R.id.btn_cash_history:
            {
                Intent intent = new Intent(this,MyCashHistoryActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_cash_protocol:
                String url = "http://" + NetWorkMg.IP_ADDRESS + "/thinkphp/file/html/xq_protocol.html";
                DefaultWebActivity.startWeb(MyProfitActivity.this,url,"提现规则");
                break;
            case R.id.btn_cash:
                if(mIsCanExchange) {
                    //提现
                }else {
                    //不可提现，给出提示
                    showTipNoneCash();
                }
                break;
        }
    }

    private void showTipNoneCash() {
        if(mBGetProfit == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String text = "";
        if(mBGetProfit.getSelf_allCount() < mBGetProfit.getAllCount()) {
            text += "你建房次数" + mBGetProfit.getSelf_allCount() + ",未达到"
                    + mBGetProfit.getAllCount()  + "次，还未具备提现的资格";
        }else {
            if(mBGetProfit.getSelf_successCount() < mBGetProfit.getSuccessCount()) {
                text += "你配对成功次数" + mBGetProfit.getSelf_successCount() + ",小于" + mBGetProfit.getSuccessCount()
                        + "次，还未具备提现的资格";
            }else {
                if(mBGetProfit.getSelf_rate() >= mBGetProfit.getReportRate()) {
                    text += "你被举报场次数" + mBGetProfit.getSelf_reportCount()  + "次，";
                    text += "建房总次数" + mBGetProfit.getSelf_allCount() + "次,";
                    text += "被举报比率为" + mBGetProfit.getSelf_rate()*100 + "%" + "超过了"
                            + mBGetProfit.getReportRate()*100 + "%，不具备提现的资格";
                }
            }
        }
        builder.setTitle("小提示")
                .setMessage(text)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();

    }
}
