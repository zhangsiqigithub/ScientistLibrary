package com.scientist.testlibrariesdemo;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.scientist.testlibrariesdemo.database.DemoDataBase;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/7/19
 * Time: 14:52
 * Desc:
 */
public class DemoApplication extends Application {

    private static DemoApplication sInstance;
    public static DemoApplication getInstance() {
        synchronized (DemoApplication.class) {
            return sInstance;
        }
    }

    private DemoDataBase mDataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public DemoDataBase getDataBase() {
        if (mDataBase == null) {
            mDataBase = Room.databaseBuilder(this, DemoDataBase.class, "demo_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mDataBase;
    }
}
