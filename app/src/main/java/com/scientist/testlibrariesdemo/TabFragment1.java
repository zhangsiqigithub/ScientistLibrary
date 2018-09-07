package com.scientist.testlibrariesdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/5
 * Time: 18:20
 * Desc:
 */
public class TabFragment1 extends Fragment {

    public static TabFragment1 create(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        TabFragment1 fragment1 = new TabFragment1();
        fragment1.setArguments(bundle);
        return fragment1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title");
        TextView textView = view.findViewById(R.id.tv_tab);
        textView.setText(title);
    }
}
