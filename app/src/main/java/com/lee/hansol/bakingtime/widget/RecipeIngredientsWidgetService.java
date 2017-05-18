package com.lee.hansol.bakingtime.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.DataUtils;
import com.lee.hansol.bakingtime.utils.PrefUtils;

import java.util.Locale;

import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class RecipeIngredientsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeIngredientsRemoteViewsFactory(getApplicationContext(), intent);
    }

    private class RecipeIngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context context;
        private Ingredient[] ingredients = new Ingredient[0];
        private int appwidgetId;

        RecipeIngredientsRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            appwidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            int recipeId = PrefUtils.getInt(context, appwidgetId, 0);
            Recipe recipe = DataUtils.getRecipeObjectWithRecipeId(context, recipeId);
            if (recipe != null) ingredients = recipe.ingredients;
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
            Ingredient ingredient = ingredients[position];
            return getViewsForIngredient(ingredient);
        }

        private RemoteViews getViewsForIngredient(Ingredient ingredient) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_list_item);
            String text = String.format(Locale.getDefault(),
                    context.getString(R.string.text_widget_ingredient_placeholder),
                    ingredient.name, ingredient.quantity, ingredient.measureUnit);
            views.setTextViewText(R.id.ingredient_list_item_text, text);
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
}

