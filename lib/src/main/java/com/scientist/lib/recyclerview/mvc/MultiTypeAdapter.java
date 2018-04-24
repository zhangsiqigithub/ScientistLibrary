package com.scientist.lib.recyclerview.mvc;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/3/6
 * Time: 18:46
 * Desc: 多种数据类型的 RecyclerView.Adapter, 需要调用 setDataTypes(DataType... dataTypes) 方法
 *       设置每种方法的数据类型和绑定方式
 */
public class MultiTypeAdapter extends RecyclerView.Adapter<MultiTypeAdapter.ViewHolder> {

    private List<Object> mData;
    private RefreshHeader mRefreshHeader = new RefreshHeader();
    private LoadMoreFooter mLoadMoreFooter = new LoadMoreFooter();
    private HashMap<String/* class name */, DataType> mDataTypeMap = new HashMap<>();

    public MultiTypeAdapter(List<Object> data) {
        mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        Object o = mData.get(position);
        String className = o.getClass().getName();
        DataType type = mDataTypeMap.get(className);
        if (type == null) {
            throw new IllegalStateException(className  + " not set a DataType");
        }
        return type.getLayoutId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object data = mData.get(position);
        String dataClassName = data.getClass().getName();
        DataType dataType = mDataTypeMap.get(dataClassName);
        if (dataType != null) {
            dataType.onBindHolder(data, holder);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setDataTypes(DataType... dataTypes) {
        mDataTypeMap.clear();
        for (DataType dataType : dataTypes) {
            mDataTypeMap.put(dataType.getTypeClassName(), dataType);
        }
        mDataTypeMap.put(mRefreshHeader.getTypeClassName(), mRefreshHeader);
        mDataTypeMap.put(mLoadMoreFooter.getTypeClassName(), mLoadMoreFooter);
    }

    public void setRefreshView(int layoutId) {
        mRefreshHeader.setLayoutId(layoutId);
    }

    public void setLoadMoreView(int layoutId) {
        mLoadMoreFooter.setLayoutId(layoutId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> mViews;

        public ViewHolder(View itemView) {
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

        public ViewHolder setText(int id, CharSequence charSequence) {
            TextView textView = getView(id);
            textView.setText(charSequence);
            return this;
        }

        public ViewHolder setText(int id, @StringRes int resId) {
            TextView textView = getView(id);
            textView.setText(resId);
            return this;
        }

        public ViewHolder setVisibility(int id, int visibility) {
            getView(id).setVisibility(visibility);
            return this;
        }

        public ViewHolder setOnClickListener(int id, View.OnClickListener onClickListener) {
            getView(id).setOnClickListener(onClickListener);
            return this;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView instanceof LoadMoreRecyclerView) {
            LoadMoreRecyclerView view = (LoadMoreRecyclerView) recyclerView;

            view.setOnRefreshSuccessListener(() -> {
                if (mData.contains(mRefreshHeader)) {
                    mData.remove(mRefreshHeader);
                    notifyItemRemoved(0);
                }
            });

            view.setOnRefreshingListener(() -> {
                if (!mData.isEmpty() && !mData.contains(mRefreshHeader)) {
                    mData.add(0, mRefreshHeader);
                    notifyItemInserted(0);
                }
            });

            view.setOnLoadMoreSuccessListener(() -> {
                mData.remove(mLoadMoreFooter);
                notifyDataSetChanged();
            });

            view.setOnLoadingMoreListener(() -> {
                if (!mData.isEmpty() && !mData.contains(mLoadMoreFooter)) {
                    mData.add(mLoadMoreFooter);
                    notifyItemInserted(getItemCount() - 1);
                }
            });

            view.setOnLoadMoreFailedListener(() -> {
                mData.remove(mLoadMoreFooter);
                notifyItemRemoved(getItemCount() - 1);
            });

            view.setOnLoadNoMoreListener(() -> {
                mData.remove(mLoadMoreFooter);
                notifyItemRemoved(getItemCount() - 1);
            });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
