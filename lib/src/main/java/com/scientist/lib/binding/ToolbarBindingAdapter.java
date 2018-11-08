package com.scientist.lib.binding;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayMap;
import android.support.annotation.MenuRes;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;

import com.scientist.lib.recyclerview.mvvm.command.ReplyCommand;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/11/8
 * Time: 16:44
 * Desc:
 */
public class ToolbarBindingAdapter {

    @BindingAdapter(value = {"menuId"})
    public static void bindMenu(Toolbar toolbar, @MenuRes int menu) {
        toolbar.inflateMenu(menu);
    }

    @BindingAdapter(value = {"menuItemVisibility"})
    public static void bindMenuItemsVisibilities(Toolbar toolbar, ObservableArrayMap<Integer, Boolean> visibilities) {
        if (visibilities != null) {
            for (int i = 0; i < visibilities.size(); i ++) {
                toolbar.getMenu().findItem(visibilities.keyAt(i)).setVisible(visibilities.valueAt(i));
            }
        }
    }

    @BindingAdapter({"menuItemsClick"})
    public static void bindMenuItemsClick(Toolbar toolbar, SparseArray<ReplyCommand> sparseArray) {
        toolbar.setOnMenuItemClickListener(item -> {
            ReplyCommand command = sparseArray.get(item.getItemId());
            if (command == null) {
                command = sparseArray.get(item.getGroupId());
            }
            if (command != null) {
                command.execute();
                return true;
            }
            return false;
        });
    }

    @BindingAdapter({"menuHomeClick"})
    public static void bindMenuHomeClick(Toolbar toolbar, ReplyCommand command) {
        toolbar.setNavigationOnClickListener(v -> {
            if (command != null) {
                command.execute();
            }
        });
    }
}
