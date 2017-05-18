package com.lee.hansol.bakingtime.utils;

import android.content.Context;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Step;

import java.util.Locale;

public class StringUtils {

    public static String getStepShortDescText(Context context,Step step) {
        return String.format(Locale.getDefault(),
                context.getString(R.string.text_step_placeholder),
                step.stepOrder, step.shortDescription);
    }
}
