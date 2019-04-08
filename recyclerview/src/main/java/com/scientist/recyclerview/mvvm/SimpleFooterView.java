package com.scientist.recyclerview.mvvm;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copyright © 2017 Worktile. All Rights Reserved.
 * Author: Moki
 * Email: mosicou@gmail.com
 * Date: 2017/9/15
 * Time: 14:39
 * Desc:
 */

public class SimpleFooterView {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_SUCCEED, STATE_LOADING, STATE_FAILED, STATE_NO_MORE})
    public @interface State {}
    public static final int STATE_SUCCEED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_FAILED = 2;
    public static final int STATE_NO_MORE = 3;
    public static @State int stateByValue(int value) {
        switch (value) {
            case 0:
                return STATE_SUCCEED;
            case 1:
                return STATE_LOADING;
            case 2:
                return STATE_FAILED;
            case 3:
                return STATE_NO_MORE;
            default:
                return STATE_SUCCEED;
        }
    }
}
