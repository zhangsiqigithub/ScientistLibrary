package com.scientist.lib.recyclerview.mvc;


import com.scientist.recyclerviewlib.R;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/4/3
 * Time: 19:05
 * Desc: 加载更多底部item
 */
public class LoadMoreFooter extends DataType<LoadMoreFooter> {
    public LoadMoreFooter() {
        super(LoadMoreFooter.class, R.layout.layout_load_more);
    }

    @Override
    public void onBindHolder(LoadMoreFooter loadMoreFooter, MultiTypeAdapter.ViewHolder viewHolder) {

    }

    public void setLayoutId(int id) {
        mLayoutId = id;
    }
}
