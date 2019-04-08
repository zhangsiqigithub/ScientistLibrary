package com.scientist.recyclerview.mvc;


import com.scientist.recyclerview.R;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/4/10
 * Time: 15:09
 * Desc: 下拉刷新头部item
 */
public class RefreshHeader extends DataType<RefreshHeader>{

    public RefreshHeader() {
        super(RefreshHeader.class, R.layout.layout_refresh);
    }

    @Override
    public void onBindHolder(RefreshHeader refreshHeader, MultiTypeViewHolder viewHolder) {

    }

    public void setLayoutId(int layoutId) {
        mLayoutId = layoutId;
    }
}
