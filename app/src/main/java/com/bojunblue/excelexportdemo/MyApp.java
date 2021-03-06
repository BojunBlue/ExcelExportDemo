package com.bojunblue.excelexportdemo;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.tbruyelle.rxpermissions2.BuildConfig;

/**
 * Application
 */
public class MyApp extends Application {

    /**
     * Application对象
     */
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Utils初始化
        Utils.init(this);
        // 日志是否打印
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
    }

    /**
     * 获取Application对象
     */
    public static MyApp getInstance() {
        return instance;
    }

}
