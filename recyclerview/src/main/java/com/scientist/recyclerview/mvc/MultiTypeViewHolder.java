package com.scientist.recyclerview.mvc;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/6
 * Time: 16:44
 * Desc: MultiTypeViewHolder
 */
public class MultiTypeViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;

    public MultiTypeViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    public <T extends View> T getView(int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        return (T) view;
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public MultiTypeViewHolder setText(int id, CharSequence charSequence) {
        TextView textView = getView(id);
        textView.setText(charSequence);
        return this;
    }

    public MultiTypeViewHolder setText(int id, @StringRes int resId) {
        TextView textView = getView(id);
        textView.setText(resId);
        return this;
    }

    public MultiTypeViewHolder setVisibility(int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }

    public MultiTypeViewHolder setOnClickListener(int id, View.OnClickListener onClickListener) {
        getView(id).setOnClickListener(onClickListener);
        return this;
    }
}
