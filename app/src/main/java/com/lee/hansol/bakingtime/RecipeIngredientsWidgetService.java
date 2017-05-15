package com.lee.hansol.bakingtime;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.lee.hansol.bakingtime.db.BakingProvider;
import com.lee.hansol.bakingtime.db.RecipeColumns;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.DbUtils;

import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class RecipeIngredientsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeIngredientsRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class RecipeIngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Ingredient[] ingredients = new Ingredient[0];
    private int appwidgetId;

    RecipeIngredientsRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appwidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        SharedPreferences prefs = context.getSharedPreferences(RecipeAppWidgetProvider.WIDGET_PREFS_NAME, Context.MODE_PRIVATE);
        int recipeId = prefs.getInt(appwidgetId+"", 0);
        Cursor cursor = context.getContentResolver().query(BakingProvider.Recipes.CONTENT_URI,
                null, RecipeColumns.RECIPE_ID + " = ?", new String[] {recipeId+""}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Recipe recipe = DbUtils.getRecipeObjectFrom(context, cursor);
            cursor.close();
            if (recipe != null)
                ingredients = recipe.ingredients;
            else
                ingredients = new Ingredient[0];
        } else {
            ingredients = new Ingredient[0];
        }
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredients.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_list_item);
        views.setTextViewText(R.id.ingredient_list_item_text, ingredients[position].name);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
