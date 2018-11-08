package com.scientist.lib.binding;

import android.databinding.BindingAdapter;
import android.view.View;

import com.scientist.lib.recyclerview.mvvm.command.ReplyCommand;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/11/8
 * Time: 16:45
 * Desc:
 */
public class ViewBindingAdapter {

    @BindingAdapter({"clickReplyCommand"})
    public static void bindClickReplyCommand(View view, ReplyCommand replyCommand) {
        view.setOnClickListener(v -> {
            if (replyCommand != null) {
                replyCommand.execute();
            }
        });
    }

    @BindingAdapter({"clickReplyCommandView"})
    public static void bindClickReplyCommandView(View view, ReplyCommand<View> replyCommand) {

        view.setOnClickListener(v -> {
            if (replyCommand != null) {
                replyCommand.execute(v);
            }
        });
    }
}
