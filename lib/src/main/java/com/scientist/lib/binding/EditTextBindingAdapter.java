package com.scientist.lib.binding;

import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.scientist.lib.recyclerview.mvvm.command.ReplyCommand;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/11/8
 * Time: 16:46
 * Desc:
 */
public class EditTextBindingAdapter {

    @BindingAdapter({"actionEnterClick"})
    public static void bindActionEnter(EditText editText, ReplyCommand command) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (command != null) {
                command.execute();
                return true;
            }
            return false;
        });
    }

    @BindingAdapter({"selectionIndex"})
    public static void bindSelection(EditText editText, String length) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setSelection(editText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
