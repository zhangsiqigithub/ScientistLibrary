package com.scientist.lib.recyclerview.mvc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.scientist.lib.recyclerview.MultiTypeViewHolder;

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
public class MultiTypeAdapter extends RecyclerView.Adapter<MultiTypeViewHolder> {

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
    public MultiTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MultiTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(MultiTypeViewHolder holder, int position) {
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
