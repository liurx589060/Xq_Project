package com.cd.xq.module.util.interfaces;

import android.app.Activity;

public interface ICheckBlackListener {
    void onDialogCheckClick(Activity activity);
    void onResult(boolean isBlack);
}