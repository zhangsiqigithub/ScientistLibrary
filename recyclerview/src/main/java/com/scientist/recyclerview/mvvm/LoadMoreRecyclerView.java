package com.scientist.recyclerview.mvvm;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/10/29
 * Time: 16:22
 * Desc:
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private OnLoadMoreListener mOnLoadMoreListener = () -> {
    };

    private OnFooterViewSucceedStateListener mOnFooterViewSucceedStateListener = () -> {
    };
    private OnFooterViewLoadingStateListener mOnFooterViewLoadingStateListener = () -> {
    };
    private OnFooterViewFailedStateListener mOnFooterViewFailedStateListener = () -> {
    };
    private OnFooterViewNoMoreStateListener mOnFooterViewNoMoreStateListener = () -> {
    };

    private @SimpleFooterView.State int mFooterViewState = SimpleFooterView.STATE_SUCCEED;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addOnScrollListener(new PullUpToLoadMoreListener());
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public void setFooterViewState(@SimpleFooterView.State int state) {
        mFooterViewState = state;
        switch (mFooterViewState) {
            case SimpleFooterView.STATE_SUCCEED:
                mOnFooterViewSucceedStateListener.onSucceed();
                break;
            case SimpleFooterView.STATE_LOADING:
                mOnFooterViewLoadingStateListener.onLoading();
                break;
            case SimpleFooterView.STATE_FAILED:
                mOnFooterViewFailedStateListener.onFailed();
                break;
            case SimpleFooterView.STATE_NO_MORE:
                mOnFooterViewNoMoreStateListener.onNoMore();
                break;
        }
    }

    public interface OnFooterViewSucceedStateListener {
        void onSucceed();
    }

    public interface OnFooterViewLoadingStateListener {
        void onLoading();
    }

    public interface OnFooterViewFailedStateListener {
        void onFailed();
    }

    public interface OnFooterViewNoMoreStateListener {
        void onNoMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    public void setOnFooterViewSucceedStateListener(OnFooterViewSucceedStateListener listener) {
        mOnFooterViewSucceedStateListener = listener;
    }

    public void setOnFooterViewLoadingStateListener(OnFooterViewLoadingStateListener listener) {
        mOnFooterViewLoadingStateListener = listener;
    }

    public void setOnFooterViewFailedStateListener(OnFooterViewFailedStateListener listener) {
        mOnFooterViewFailedStateListener = listener;
    }

    public void setOnFooterViewNoMoreStateListener(OnFooterViewNoMoreStateListener listener) {
        mOnFooterViewNoMoreStateListener = listener;
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

    private boolean isToEnd(RecyclerView recyclerView) {
        View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
        if (lastChildView == null) return false;

        int[] lastChildPosition = new int[2];
        lastChildView.getLocationInWindow(lastChildPosition);
        int lastChildBottom = lastChildView.getHeight() + lastChildPosition[1];

        int[] recyclerViewPosition = new int[2];
        recyclerView.getLocationInWindow(recyclerViewPosition);
        int recyclerBottom = recyclerView.getHeight() + recyclerViewPosition[1] - recyclerView.getPaddingBottom();

        int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);

        return lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1;

    }

    private class LoadMoreScrollListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                boolean isEnd = isToEnd(recyclerView);

                if (isOverScreen(recyclerView) && isEnd && mFooterViewState == SimpleFooterView.STATE_SUCCEED) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    }

    private class PullUpToLoadMoreListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == SCROLL_STATE_DRAGGING) {
                if (!canScrollVertically(1)) {
                    if (mFooterViewState == SimpleFooterView.STATE_SUCCEED || mFooterViewState == SimpleFooterView.STATE_FAILED) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0 && getScrollState() == SCROLL_STATE_DRAGGING) {
                if (!canScrollVertically(1)) {
                    if (mFooterViewState == SimpleFooterView.STATE_SUCCEED || mFooterViewState == SimpleFooterView.STATE_FAILED) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        }
    }
}
