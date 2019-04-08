package com.scientist.recyclerview.mvvm;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import com.scientist.recyclerview.R;
import com.scientist.recyclerview.BR;


/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/10/29
 * Time: 16:36
 * Desc:
 */
public class FooterViewModel implements ItemViewModel {

    public ObservableInt mProgressBarVisibility = new ObservableInt();
    public ObservableField<String> mHint = new ObservableField<>("");

    @Override
    public int getLayoutId() {
        return R.layout.layout_load_more_mvvm;
    }

    @Override
    public int getVariableId() {
        return BR.viewModel;
    }

    @Override
    public ExtraViewModel[] getExtraViewModels() {
        return new ExtraViewModel[0];
    }

    public void setState(@SimpleFooterView.State int state) {
        switch (state) {
            case SimpleFooterView.STATE_SUCCEED:
                break;
            case SimpleFooterView.STATE_LOADING:
                mProgressBarVisibility.set(View.VISIBLE);
                mHint.set("加载更多");
                break;
            case SimpleFooterView.STATE_FAILED:
                mProgressBarVisibility.set(View.GONE);
                mHint.set("加载失败");
                break;
            case SimpleFooterView.STATE_NO_MORE:
                mProgressBarVisibility.set(View.GONE);
                mHint.set("没有更多");
                break;
        }
    }
}
