package com.scientist.lib.recyclerview.paging;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.arch.paging.PositionalDataSource;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.scientist.lib.recyclerview.MultiTypeViewHolder;
import com.scientist.lib.recyclerview.mvc.DataType;

import java.util.HashMap;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/6
 * Time: 16:20
 * Desc: MultiTypeAdapter for Android Paging Library
 */
public class MultiTypePageListAdapter extends PagedListAdapter<Different, MultiTypeViewHolder> {

    private HashMap<String/* class name */, DataType> mDataTypeMap = new HashMap<>();
    private static DiffUtil.ItemCallback<Different> sDiffCallback = new DiffUtil.ItemCallback<Different>() {
        @Override
        public boolean areItemsTheSame(Different oldItem, Different newItem) {
            return oldItem.getClass().getName().equals(newItem.getClass().getName())
                    && oldItem.uniqueMark().equals(newItem.uniqueMark());
        }

        @Override
        public boolean areContentsTheSame(Different oldItem, Different newItem) {
            return oldItem.getClass().getName().equals(newItem.getClass().getName())
                    && oldItem.contentUniqueMark().equals(newItem.contentUniqueMark());
        }
    };

    public MultiTypePageListAdapter() {
        super(sDiffCallback);
    }

    public void setDataTypes(DataType... dataTypes) {
        mDataTypeMap.clear();
        for (DataType dataType : dataTypes) {
            mDataTypeMap.put(dataType.getTypeClassName(), dataType);
        }
    }

    public void initializeLoading(int pageSize, int prefetchDistance, InitialCallback initialCallback, LoadMoreCallback loadMoreCallback) {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(pageSize)
                .setPrefetchDistance(prefetchDistance)
                .build();

        PositionalDataSource<Different> dataSource = new PositionalDataSource<Different>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Different> callback) {
                initialCallback.onInitial(params, callback);
            }

            @Override
            public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Different> callback) {
                loadMoreCallback.onLoadMore(params, callback);
            }
        };

        PagedList<Different> pagedList = new PagedList.Builder(dataSource, config)
                .setFetchExecutor(command -> new Thread(command).start())
                .setNotifyExecutor(command -> new Handler(Looper.getMainLooper()).post(command))
                .setBoundaryCallback(new PagedList.BoundaryCallback() {
                    @Override
                    public void onItemAtEndLoaded(@NonNull Object itemAtEnd) {
                        super.onItemAtEndLoaded(itemAtEnd);
                    }
                })
                .build();

        submitList(pagedList);
    }

    @Override
    public int getItemViewType(int position) {
        Object o = getItem(position);
        String className = o.getClass().getName();
        DataType type = mDataTypeMap.get(className);
        if (type == null) {
            throw new IllegalStateException(className  + " not set a DataType");
        }
        return type.getLayoutId();
    }


    @NonNull
    @Override
    public MultiTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MultiTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MultiTypeViewHolder holder, int position) {
        Object data = getItem(position);
        String dataClassName = data.getClass().getName();
        DataType dataType = mDataTypeMap.get(dataClassName);
        if (dataType != null) {
            dataType.onBindHolder(data, holder);
        }
    }


    public interface InitialCallback {
        void onInitial(@NonNull PositionalDataSource.LoadInitialParams params, @NonNull PositionalDataSource.LoadInitialCallback<Different> callback);
    }

    public interface LoadMoreCallback {
        void onLoadMore(@NonNull PositionalDataSource.LoadRangeParams params, @NonNull PositionalDataSource.LoadRangeCallback<Different> callback);
    }
}
