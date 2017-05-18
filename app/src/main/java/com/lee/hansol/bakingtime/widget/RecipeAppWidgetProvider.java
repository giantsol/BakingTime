package com.lee.hansol.bakingtime.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.DataUtils;
import com.lee.hansol.bakingtime.utils.PrefUtils;

public class RecipeAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        int recipeId = PrefUtils.getInt(context, appWidgetId, 0);
        Recipe recipe = DataUtils.getRecipeObjectWithRecipeId(context, recipeId);
        RemoteViews views = (recipe != null)
                ? getViewsForRecipe(context, recipe, appWidgetId)
                : null;
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static RemoteViews getViewsForRecipe(Context context, @NonNull Recipe recipe, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        views.setTextViewText(R.id.appwidget_recipe_name, recipe.name);
        Intent serviceIntent = getServiceIntentForRemoteAdapter(context, appWidgetId);
        views.setRemoteAdapter(R.id.appwidget_ingredients, serviceIntent);
        views.setEmptyView(R.id.appwidget_ingredients, R.id.appwidget_ingredients_empty);
        return views;
    }

    private static Intent getServiceIntentForRemoteAdapter(Context context, int appWidgetId) {
        Intent serviceIntent = new Intent(context, RecipeIngredientsWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        return serviceIntent;
    }
}
