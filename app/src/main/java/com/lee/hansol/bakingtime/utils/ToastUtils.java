package com.lee.hansol.bakingtime.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast;

    public static void toast(Context context, String text, int duration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }

    public static void toast(Context context, String text) {
        toast(context, text, Toast.LENGTH_SHORT);
    }
}
