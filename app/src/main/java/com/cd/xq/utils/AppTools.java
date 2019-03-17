package com.cd.xq.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.cd.xq.R;
import com.cd.xq.beans.JMOnlineParam;
import com.cd.xq.manager.AppDataManager;
import com.cd.xq.module.util.Constant;
import com.cd.xq.module.util.beans.jmessage.JMSendBean;
import com.cd.xq.module.util.beans.user.UserInfoBean;
import com.cd.xq.module.util.jmessage.JMsgSender;
import com.cd.xq.module.util.manager.DataManager;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2019/2/13.
 */

public class AppTools {

    /**
     * 告知好友是否上线或离线
     * @param isOnLine
     */
    public static void notifyFriendOnLine(boolean isOnLine) {
        for(int i = 0; i < AppDataManager.getInstance().getFriendList().size() ; i++) {
            UserInfoBean userInfoBean = AppDataManager.getInstance().getFriendList().get(i);
            notifyFriendOnLineByUser(userInfoBean,isOnLine);
        }
    }

    public static void notifyFriendOnLineByUser(UserInfoBean userInfoBean,boolean isOnLine) {
        JMSendBean sendBean = new JMSendBean();
        sendBean.setCode(JMSendBean.JM_SEND_USER_CHECK_ONLINE);
        sendBean.setTargetUserName(userInfoBean.getUser_name());
        sendBean.setFromUserName(DataManager.getInstance().getUserInfo().getUser_name());
        JMOnlineParam param = new JMOnlineParam();
        param.setType(JMOnlineParam.TYPE_SEND);
        param.setOnLine(isOnLine);
        sendBean.setJsonData(new Gson().toJson(param));
        JMsgSender.sendTextMessage(sendBean);
    }

    /**
     * 将文字转换成图片
     * @param text
     * @return
     */
    public static SpannableString convertSpan(Context context,String text, int width, int height) {
        SpannableString spannable = new SpannableString(text);
        String strFlags[] = {"[建房卡]","[礼品卡]","[入门卡]","[延时卡]"};

        for(int i = 0 ; i < strFlags.length ; i++) {
            int index = -1;
            int type = -1;
            String strFlag = strFlags[i];
            if(i == 0) {
                type = Constant.CARD_JIANFANG;
            }else if(i == 1) {
                type = Constant.CARD_LIPIN;
            }else if(i == 2) {
                type = Constant.CARD_RUMEN;
            }else if(i == 3) {
                type = Constant.CARD_YANSHI;
            }

            while((index = text.indexOf(strFlag,index))!=-1){
                //每循环一次，就要明确下一次查找的起始位置。
                int lastIndex = index + strFlag.length();
                //处理图片
                //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
                ImageSpan imageSpan = getImageSpan(context,type,width,height);
                if(imageSpan != null) {
                    spannable.setSpan(imageSpan, index,lastIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                index = lastIndex;
                //每查找一次，count自增。
            }
        }

        return spannable;
    }

    /**
     * 获取ImageSpan
     * @param type
     * @param width
     * @param height
     * @return
     */
    private static ImageSpan getImageSpan(Context context,int type, int width, int height) {
        Drawable drawable = null;
        ImageSpan span = null;
        if(type == Constant.CARD_JIANFANG) {
            //建房卡
            drawable = context.getResources().getDrawable(R.drawable.card_jiangfang);
        }else if(type == Constant.CARD_LIPIN) {
            //礼品卡
            drawable = context.getResources().getDrawable(R.drawable.card_liping);
        }else if(type == Constant.CARD_RUMEN) {
            //入门卡
            drawable = context.getResources().getDrawable(R.drawable.card_rumen);
        }else if(type == Constant.CARD_YANSHI) {
            //延时卡
            drawable = context.getResources().getDrawable(R.drawable.card_yanshi);
        }
        if(drawable != null) {
            drawable.setBounds(0, 0, width,height);
            span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        }
        return span;
    }
}
