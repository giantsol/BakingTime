package com.lee.hansol.bakingtime.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.DataUtils;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipesLoaderFromDb extends AsyncTaskLoader<Recipe[]> {
    private Recipe[] recipes;

    public RecipesLoaderFromDb(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        return DataUtils.loadRecipesFromDb(getContext());
    }

    @Override
    public void deliverResult(@NonNull Recipe[] data) {
        recipes = data;
        super.deliverResult(data);
    }
}
