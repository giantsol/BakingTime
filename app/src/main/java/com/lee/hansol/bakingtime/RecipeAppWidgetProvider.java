package com.lee.hansol.bakingtime;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.lee.hansol.bakingtime.models.Recipe;

public class RecipeAppWidgetProvider extends AppWidgetProvider {
    public static final String WIDGET_PREFS_NAME = "widget_pref";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {
        Intent intent = new Intent(context, RecipeIngredientsWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        SharedPreferences prefs = context.getSharedPreferences(WIDGET_PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(""+appWidgetId, recipe.recipeId).apply();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        views.setTextViewText(R.id.appwidget_recipe_name, recipe.name);
        views.setRemoteAdapter(R.id.appwidget_ingredients, intent);
        views.setEmptyView(R.id.appwidget_ingredients, R.id.appwidget_ingredients_empty);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}
