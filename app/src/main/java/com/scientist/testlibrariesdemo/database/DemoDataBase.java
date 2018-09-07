package com.scientist.testlibrariesdemo.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.scientist.testlibrariesdemo.data.Product;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/7/19
 * Time: 14:46
 * Desc:
 */
@Database(entities = {Product.class}, version = 2, exportSchema = false)
public abstract class DemoDataBase extends RoomDatabase {

    public abstract ProductDao productDao();
}
