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
        log(R.string.log_on_start_loading_from_internet_loader);
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        log(R.string.log_load_in_background_from_internet_loader);
        String urlString = getContext().getString(R.string.url_baking_recipe);
        try {
            Recipe[] recipes = DataUtils.loadRecipesFromUrl(getContext(), urlString);
            int insertedRowNum = DataUtils.saveRecipesToDb(getContext(), recipes);
            log(String.format(Locale.getDefault(),
                    getContext().getString(R.string.log_inserted_row_count_placeholder),
                    insertedRowNum));
            return recipes;
        } catch (Exception e) {
            e.printStackTrace();
            return emptyRecipes;
        }
    }

    @Override
    public void deliverResult(@NonNull Recipe[] data) {
        log(R.string.log_deliver_result_from_internet_loader);
        recipes = data;
        super.deliverResult(data);
    }
}
