package com.scientist.recyclerview.mvc;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/3/8
 * Time: 16:03
 * Desc: MultiTypeAdapter 的数据类型
 */
public abstract class DataType <T> {
    private String mTypeClassName;
    protected int mLayoutId;

    public abstract void onBindHolder(T t, MultiTypeViewHolder viewHolder);

    public DataType(Class<T> clazz, int layoutId) {
        mLayoutId = layoutId;
        mTypeClassName = clazz.getName();
    }

    public String getTypeClassName() {
        return mTypeClassName;
    }

    public int getLayoutId() {
        return mLayoutId;
    }
}
