package com.scientist.lib.recyclerview.mvvm;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;

import com.scientist.lib.recyclerview.mvvm.command.ReplyCommand;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/4/24
 * Time: 11:19
 * Desc: DataBinding Adapter
 */
public class ViewBindingAdapter {

    @BindingAdapter({"footerState"})
    public static void setFooterState(LoadMoreRecyclerView recyclerView,
                                      @SimpleFooterView.State int state) {
        recyclerView.setFooterViewState(state);
    }

    @BindingAdapter({"loadMoreCommand"})
    public static void setLoadMore(LoadMoreRecyclerView recyclerView,
                                   ReplyCommand command) {
        recyclerView.setOnLoadMoreListener(command::execute);
    }

    @BindingAdapter(value = {"data"}, requireAll = false)
    public static void setRecyclerViewData(RecyclerView recyclerView,
                                           ObservableArrayList<ItemViewModel> viewModels) {

        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new ViewModelRecyclerViewAdapter(viewModels);
            recyclerView.setAdapter(adapter);
        }

        viewModels.addOnListChangedCallback(new ListChangedListener(adapter));
    }

    private static class ListChangedListener extends ObservableList.OnListChangedCallback<ObservableArrayList<ItemViewModel>> {

        private RecyclerView.Adapter adapter;
        private LongSparseArray<Boolean> blockingArray = new LongSparseArray<>();

        ListChangedListener(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onChanged(ObservableArrayList<ItemViewModel> sender) {
            long threadId = Thread.currentThread().getId();
            long mainThreadId = Looper.getMainLooper().getThread().getId();

            if (threadId == mainThreadId) {
                adapter.notifyDataSetChanged();
            } else {
                blockingArray.put(threadId, true);
                Observable.just(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> blockingArray.put(threadId, false))
                        .subscribe(integer -> adapter.notifyDataSetChanged());

                while (blockingArray.get(threadId)) {
                    // loop
                }
            }
        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<ItemViewModel> sender,
                                       int positionStart,
                                       int itemCount) {
            long threadId = Thread.currentThread().getId();
            long mainThreadId = Looper.getMainLooper().getThread().getId();

            if (threadId == mainThreadId) {
                adapter.notifyItemRangeChanged(positionStart, itemCount);
            } else {
                blockingArray.put(threadId, true);
                Observable.just(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> blockingArray.put(threadId, false))
                        .subscribe(integer -> adapter.notifyItemRangeChanged(positionStart, itemCount));

                while (blockingArray.get(threadId)) {
                    // loop
                }
            }
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<ItemViewModel> sender,
                                        int positionStart,
                                        int itemCount) {
            long threadId = Thread.currentThread().getId();
            long mainThreadId = Looper.getMainLooper().getThread().getId();

            if (threadId == mainThreadId) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
            } else {
                blockingArray.put(threadId, true);
                Observable.just(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> blockingArray.put(threadId, false))
                        .subscribe(integer -> adapter.notifyItemRangeInserted(positionStart, itemCount));

                while (blockingArray.get(threadId)) {
                    // loop
                }
            }
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<ItemViewModel> sender,
                                     int fromPosition,
                                     int toPosition,
                                     int itemCount) {
            long threadId = Thread.currentThread().getId();
            long mainThreadId = Looper.getMainLooper().getThread().getId();

            if (threadId == mainThreadId) {
                adapter.notifyDataSetChanged();
            } else {
                Observable.just(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> blockingArray.put(threadId, false))
                        .subscribe(integer -> adapter.notifyDataSetChanged());

                while (blockingArray.get(threadId)) {
                    // loop
                }
            }
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<ItemViewModel> sender,
                                       int positionStart,
                                       int itemCount) {
            long threadId = Thread.currentThread().getId();
            long mainThreadId = Looper.getMainLooper().getThread().getId();

            if (threadId == mainThreadId) {
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
            } else {
                Observable.just(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> blockingArray.put(threadId, false))
                        .subscribe(integer -> adapter.notifyItemRangeRemoved(positionStart, itemCount));

                while (blockingArray.get(threadId)) {
                    // loop
                }
            }
        }
    }
}
