package com.lee.hansol.bakingtime.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.DataUtils;

import java.util.Locale;

import static com.lee.hansol.bakingtime.utils.DataUtils.emptyRecipes;
import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipesLoaderFromInternet extends AsyncTaskLoader<Recipe[]> {
    private Recipe[] recipes;

    public RecipesLoaderFromInternet(Context context) { super(context); }

    @Override
    protected void onStartLoading() {
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        String urlString = getContext().getString(R.string.url_baking_recipe);
        try {
            Recipe[] recipes = DataUtils.loadRecipesFromUrl(getContext(), urlString);
            DataUtils.saveRecipesToDb(getContext(), recipes);
            return recipes;
        } catch (Exception e) {
            e.printStackTrace();
            return emptyRecipes;
        }
    }

    @Override
    public void deliverResult(@NonNull Recipe[] data) {
        recipes = data;
        super.deliverResult(data);
    }
}
