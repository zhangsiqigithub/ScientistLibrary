package com.scientist.testlibrariesdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.scientist.lib.recyclerview.MultiTypeViewHolder;
import com.scientist.lib.recyclerview.mvc.DataType;
import com.scientist.lib.recyclerview.paging.Different;
import com.scientist.lib.recyclerview.paging.MultiTypePageListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/6
 * Time: 17:13
 * Desc:
 */
public class MultiTypePageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MultiTypePageListAdapter mAdapter = new MultiTypePageListAdapter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multitype_page);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setDataTypes(
                new DataType<Product>(Product.class, R.layout.item_product) {
                    @Override
                    public void onBindHolder(Product product, MultiTypeViewHolder viewHolder) {
                        viewHolder.setText(R.id.tv_id, product.pid);
                        viewHolder.setText(R.id.tv_name, product.name);
                        viewHolder.setText(R.id.tv_price, product.price);
                    }
                },
                new DataType<Ad>(Ad.class, R.layout.item_ad) {

                    @Override
                    public void onBindHolder(Ad ad, MultiTypeViewHolder viewHolder) {
                        viewHolder.setText(R.id.tv_ad_id, ad.adId);
                        viewHolder.setText(R.id.tv_ad_url, ad.adUrl);
                    }
                }
        );

        mAdapter.initializeLoading(10, 2,
                (params, callback) -> {
                    List<Different> data = new ArrayList<>();
                    for (int i = 0; i < 5; i ++) {
                        if (i%2 == 0) {
                            data.add(new Product("pid " + i, "口香糖 " + i, String.valueOf(i * 3.5)));
                        } else {
                            data.add(new Ad("ad " + i, "ad url " + i));
                        }
                    }
                    callback.onResult(data, 0);
                },
                (params, callback) -> {
                    Log.d("MyTag", "loadRangeParam start position " + params.startPosition + ", loadSize " + params.loadSize);
                    List<Different> data = new ArrayList<>();
                    for (int i = params.startPosition; i < params.startPosition + 20; i ++) {
                        if (i%2 == 0) {
                            data.add(new Product("pid " + i, "茶叶 " + i, String.valueOf(i * 9.5)));
                        } else {
                            data.add(new Ad("ad " + i, "ad url " + i));
                        }
                    }
                    callback.onResult(data);
                });
    }


    public static class Product implements Different {

        public Product(String pid, String name, String price) {
            this.pid = pid;
            this.name = name;
            this.price = price;
        }

        private String pid;
        private String name;
        private String price;

        public String getPid() {
            return pid;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }

        @Override
        public Object uniqueMark() {
            return pid + name + price;
        }

        @Override
        public Object contentUniqueMark() {
            return uniqueMark();
        }
    }

    public static class Ad implements Different {

        public Ad(String adId, String adUrl) {
            this.adId = adId;
            this.adUrl = adUrl;
        }

        private String adId;
        private String adUrl;

        public String getAdId() {
            return adId;
        }

        public String getAdUrl() {
            return adUrl;
        }

        @Override
        public Object uniqueMark() {
            return adId + adUrl;
        }

        @Override
        public Object contentUniqueMark() {
            return uniqueMark();
        }
    }
}
