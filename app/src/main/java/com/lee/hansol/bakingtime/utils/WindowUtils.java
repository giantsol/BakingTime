package com.lee.hansol.bakingtime.utils;

import android.app.Activity;
import android.view.WindowManager;

public class WindowUtils {

    public static void setUntouchable(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void clearUntouchable(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
