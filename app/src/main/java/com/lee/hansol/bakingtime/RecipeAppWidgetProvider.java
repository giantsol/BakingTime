package com.lee.hansol.bakingtime;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.lee.hansol.bakingtime.models.Recipe;

public class RecipeAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        views.setTextViewText(R.id.appwidget_recipe_name, recipe.name);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}
