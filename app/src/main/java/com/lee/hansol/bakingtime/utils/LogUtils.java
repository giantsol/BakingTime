package com.lee.hansol.bakingtime.utils;


import android.content.Context;
import android.util.Log;

public class LogUtils {
    private static final String TAG = "hello";

    //still a bad practice although I set this to null in onDestroy of MainActivity?
    private static Context context = null;

    public static void setContext(Context context) { LogUtils.context = context; }

    public static void log(String msg) {
        Log.d(TAG, msg);
    }

    public static void log(int stringId) {
        if (context == null) return;
        String msg = context.getString(stringId);
        log(msg);
    }
}
