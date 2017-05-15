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
        log(R.string.log_on_start_loading_from_db_loader);
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        log(R.string.log_load_in_background_from_db_loader);
        return DataUtils.loadRecipesFromDb(getContext());
    }

    @Override
    public void deliverResult(@NonNull Recipe[] data) {
        log(R.string.log_deliver_result_from_db_loader);
        recipes = data;
        super.deliverResult(data);
    }
}
