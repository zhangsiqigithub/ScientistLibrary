package com.scientist.lib.recyclerview.mvc;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/3/20
 * Time: 19:26
 * Desc: 上拉加载更多的recyclerview
 */
public class LoadMoreRecyclerView extends RecyclerView{

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({REFRESH_STATE_SUCCESS, REFRESH_STATE_REFRESHING})
    public @interface RefreshState{}
    public static final int REFRESH_STATE_SUCCESS = 0;
    public static final int REFRESH_STATE_REFRESHING = 1;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOAD_MORE_STATE_SUCCESS, LOAD_MORE_STATE_LOADING, LOAD_MORE_STATE_FAILED, LOAD_MORE_STATE_NO_MORE})
    public @interface LoadMoreState {}
    public static final int LOAD_MORE_STATE_SUCCESS = 0;
    public static final int LOAD_MORE_STATE_LOADING = 1;
    public static final int LOAD_MORE_STATE_FAILED = 2;
    public static final int LOAD_MORE_STATE_NO_MORE = 3;

    private boolean mLoadMoreEnable = false;
    private OnLoadMoreListener mLoadMoreListener = () -> {};
    private OnLoadMoreSuccessListener mOnLoadMoreSuccessListener = () -> {};
    private OnLoadingMoreListener mOnLoadingListener = () -> {};
    private OnLoadMoreFailedListener mOnLoadMoreFailedListener = () -> {};
    private OnLoadMoreNoMoreListener mOnLoadNoMoreListener = () -> {};
    private @LoadMoreState int mLoadMoreState = LOAD_MORE_STATE_SUCCESS;

    private boolean mRefreshEnable = false;
    private OnRefreshListener mRefreshListener = () -> {};
    private OnRefreshSuccessListener mRefreshSuccessListener = () -> {};
    private OnRefreshingListener mRefreshingListener = () -> {};
    private @RefreshState int mRefreshState = REFRESH_STATE_SUCCESS;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnLoadMoreSuccessListener(OnLoadMoreSuccessListener listener) {
        mOnLoadMoreSuccessListener = listener;
    }

    public void setOnLoadingMoreListener(OnLoadingMoreListener listener) {
        mOnLoadingListener = listener;
    }

    public void setOnLoadMoreFailedListener(OnLoadMoreFailedListener listener) {
        mOnLoadMoreFailedListener = listener;
    }

    public void setOnLoadNoMoreListener(OnLoadMoreNoMoreListener listener) {
        mOnLoadNoMoreListener = listener;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setOnRefreshSuccessListener(OnRefreshSuccessListener listener) {
        mRefreshSuccessListener = listener;
    }

    public void setOnRefreshingListener(OnRefreshingListener listener) {
        mRefreshingListener = listener;
    }

    public void setLoadMoreState(@LoadMoreState int state) {
        mLoadMoreState = state;
        switch (state) {
            case LOAD_MORE_STATE_SUCCESS:
                mOnLoadMoreSuccessListener.onSuccess();
                break;
            case LOAD_MORE_STATE_LOADING:
                mOnLoadingListener.onLoading();
                break;
            case LOAD_MORE_STATE_FAILED:
                mOnLoadMoreFailedListener.onFailed();
                break;
            case LOAD_MORE_STATE_NO_MORE:
                mOnLoadNoMoreListener.onNoMore();
                break;
        }
    }

    public void setRefreshState(@RefreshState int state) {
        mRefreshState = state;
        switch (state) {
            case REFRESH_STATE_SUCCESS:
                mRefreshSuccessListener.onSuccess();
                break;
            case REFRESH_STATE_REFRESHING:
                mRefreshingListener.onRefreshing();
                break;
        }
    }

    private void init() {
        addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mLoadMoreEnable) return;
                if (dy > 0 && getScrollState() == SCROLL_STATE_DRAGGING) {
                    if (!canScrollVertically(1)) {
                        if (mLoadMoreState == LOAD_MORE_STATE_SUCCESS) {
                            setLoadMoreState(LOAD_MORE_STATE_LOADING);
                            mLoadMoreListener.onLoadMore();
                        }
                    }
                }

                if (!mRefreshEnable) return;
                if (dy < 0 && getScrollState() == SCROLL_STATE_DRAGGING) {
                    if (!canScrollVertically(-1)) {
                        if (mRefreshState == REFRESH_STATE_SUCCESS) {
                            setRefreshState(REFRESH_STATE_REFRESHING);
                            mRefreshListener.onRefresh();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mLoadMoreEnable) return;
                if (newState == SCROLL_STATE_DRAGGING) {
                    if (!canScrollVertically(1)) {
                        if (mLoadMoreState == LOAD_MORE_STATE_SUCCESS) {
                            setLoadMoreState(LOAD_MORE_STATE_LOADING);
                            mLoadMoreListener.onLoadMore();
                            // TODO: 2018/4/6 setting 到底部 idle 状态时，向上滑也会加载
                        }
                    }
                }
            }
        });
    }

    private boolean isToEnd(RecyclerView recyclerView) {
        View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
        if (lastChildView == null) return false;

        int[] lastChildPosition = new int[2];
        lastChildView.getLocationInWindow(lastChildPosition);
        int lastChildBottom = lastChildView.getHeight() + lastChildPosition[1];

        int[] recyclerViewPosition = new int[2];
        recyclerView.getLocationInWindow(recyclerViewPosition);
        int recyclerBottom = recyclerView.getHeight() + recyclerViewPosition[1] - recyclerView.getPaddingBottom();

        int lastPosition  = recyclerView.getLayoutManager().getPosition(lastChildView);

        return lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1;

    }

    private boolean isOverScreen(RecyclerView recyclerView) {
        View firstChildView = recyclerView.getLayoutManager().getChildAt(0);
        if (firstChildView == null) return false;

        int[] firstChildPosition = new int[2];
        firstChildView.getLocationInWindow(firstChildPosition);
        int firstChildTop = firstChildPosition[1];

        int[] recyclerViewPosition = new int[2];
        recyclerView.getLocationInWindow(recyclerViewPosition);
        int recyclerTop = recyclerViewPosition[1] + recyclerView.getPaddingTop();

        int firstPosition = recyclerView.getLayoutManager().getPosition(firstChildView);

        return firstPosition != 0 || firstChildTop < recyclerTop;
    }

    public void setLoadMoreEnable(boolean enable) {
        mLoadMoreEnable = enable;
    }

    public void setRefreshEnable(boolean enable) {
        mRefreshEnable = enable;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnLoadMoreSuccessListener {
        void onSuccess();
    }

    public interface OnLoadingMoreListener {
        void onLoading();
    }

    public interface OnLoadMoreFailedListener {
        void onFailed();
    }

    public interface OnLoadMoreNoMoreListener {
        void onNoMore();
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnRefreshSuccessListener {
        void onSuccess();
    }

    public interface OnRefreshingListener {
        void onRefreshing();
    }
}
