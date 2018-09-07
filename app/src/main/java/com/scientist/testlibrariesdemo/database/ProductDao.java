package com.scientist.testlibrariesdemo.database;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.scientist.testlibrariesdemo.data.Product;

import java.util.List;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/7/19
 * Time: 11:51
 * Desc:
 */
@Dao
public interface ProductDao {

    @Query(value = "select * from product order by productId ASC")
    DataSource.Factory<Integer, Product> allProduct();

    @Query(value = "select * from product")
    List<Product> allProductList();

    @Query(value = "select * from product order by productId ASC limit 10 ")
    List<Product> smallestId10();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Product> products);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(List<Product> products);

    @Delete
    void delete(List<Product> products);
}
