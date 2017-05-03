package com.lee.hansol.bakingtime.utils;

import android.content.Context;

import com.lee.hansol.bakingtime.R;

import java.util.Locale;

public class ExceptionUtils {

    public static void throwUnknownIdException(Context context, int id) {
        throw new RuntimeException(
                String.format(Locale.getDefault(),
                        context.getString(R.string.exception_unknown_id_placeholder), id)
        );
    }
}
