package com.scientist.testlibrariesdemo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;

import com.scientist.testlibrariesdemo.data.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/7/19
 * Time: 10:43
 * Desc:
 */
public class PagingViewModel extends AndroidViewModel {

    private static final int PAGE_SIZE = 8;
    private static final int PRE_DISTANCE = 5;

    private LiveData<PagedList<Product>> mProductsLiveData;

    public PagingViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<PagedList<Product>> getProductsLiveData() {
        initPagedList();
        return mProductsLiveData;
    }

    private void initPagedList() {

        PagedList.Config config = new PagedList
                .Config
                .Builder()
                .setPageSize(PAGE_SIZE)
                .setPrefetchDistance(PRE_DISTANCE)
                .setEnablePlaceholders(false)
                .build();

        PositionalDataSource<Product> dataSource = new PositionalDataSource<Product>() {
            @Override
            public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Product> callback) {
                List<Product> products = new ArrayList<>();
                for (int i = 0; i < 30; i ++) {
                    products.add(new Product("first name " + i, i, + i * 2.3));
                }
                callback.onResult(products, 0);
            }

            @SuppressLint("CheckResult")
            @Override
            public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Product> callback) {


                List<Product> products = new ArrayList<>();
                Observable.just(products)
                        .delay(2, TimeUnit.SECONDS)
                        .doOnNext(products1 -> {
                            for (int i = 0; i < 10; i ++) {
                                products1.add(new Product("follow name " + i, i, + i * 2.3));
                            }
                        })
                        .subscribe(callback::onResult);


            }
        };

        DataSource.Factory<Integer, Product> factory = new DataSource.Factory<Integer, Product>() {
            @Override
            public DataSource<Integer, Product> create() {
                return dataSource;
            }
        };

        mProductsLiveData = new LivePagedListBuilder<>(
                factory,
                config
        ).build();

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
