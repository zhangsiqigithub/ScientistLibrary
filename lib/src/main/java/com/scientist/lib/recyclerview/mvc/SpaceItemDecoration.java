package com.scientist.lib.recyclerview.mvc;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.scientist.lib.util.UnitConversion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/3/2
 * Time: 17:31
 * Desc: recyclerview 分割线
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEFT, TOP, RIGHT, BOTTOM})
    @interface Direction {}
    public static final int LEFT = 1;
    public static final int TOP = 2;
    public static final int RIGHT = 3;
    public static final int BOTTOM = 4;


    private int mSpaceValue;
    private Set<Integer> mDirectionsSet;
    public SpaceItemDecoration(Context context, int spaceValueInDp) {
        this(context, spaceValueInDp, BOTTOM);
    }
    public SpaceItemDecoration(Context context, int spaceValueInDp, @Direction Integer... directions) {
        mSpaceValue = UnitConversion.dp2px(context, spaceValueInDp);
        mDirectionsSet = new HashSet<>(Arrays.asList(directions));
    }

    public int getSpace() {
        return mSpaceValue;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.bottom = mSpaceValue;
        if (mDirectionsSet.contains(LEFT)) {
            outRect.left = mSpaceValue;
        }
        if (mDirectionsSet.contains(TOP)) {
            outRect.top = mSpaceValue;
        }
        if (mDirectionsSet.contains(RIGHT)) {
            outRect.right = mSpaceValue;
        }

    }
}
