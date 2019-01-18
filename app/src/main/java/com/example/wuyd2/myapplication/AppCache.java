package com.example.wuyd2.myapplication;


import android.content.Context;

/**
 * Created by fudiandong on 2018/7/17
 * PackageName is com.wohome
 */
public class AppCache {

    private static Context context;

    private static String account;


    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    private static boolean mainTaskLaunching;

    public static void setAccount(String account) {
        AppCache.account = account;
    }


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AppCache.context = context.getApplicationContext();
    }

    public static void setMainTaskLaunching(boolean mainTaskLaunching) {
        AppCache.mainTaskLaunching = mainTaskLaunching;
    }

    public static boolean isMainTaskLaunching() {
        return mainTaskLaunching;
    }
}
