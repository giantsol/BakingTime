package com.lee.hansol.bakingtime.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
    private static Toast sToast;

    public static void toast(Context context, String text, int duration) {
        if (sToast != null) sToast.cancel();
        sToast = Toast.makeText(context, text, duration);
        sToast.show();
    }

    public static void toast(Context context, String text) {
        toast(context, text, Toast.LENGTH_SHORT);
    }

    public static boolean isToastShowingWithMsg(String msg) {
        String shownMessage = getToastMessage();
        return shownMessage != null && shownMessage.equals(msg);
    }

    @Nullable
    private static String getToastMessage() {
        if ((sToast == null) || !sToast.getView().isShown()) return null;
        TextView textView = (TextView) sToast.getView().findViewById(android.R.id.message);
        return textView.getText().toString();
    }
}
