package com.lee.hansol.bakingtime.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static final String PREFERENCES_FILE_NAME = "baking_prefs";

    public static void putInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(key, value).apply();
    }

    public static void putInt(Context context, int key, int value) {
        putInt(context, String.valueOf(key), value);
    }

    public static int getInt(Context context, String key, int defVal) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(key, defVal);
    }

    public static int getInt(Context context, int key, int defVal) {
        return getInt(context, String.valueOf(key), defVal);
    }
}
