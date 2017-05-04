package com.lee.hansol.bakingtime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.lee.hansol.bakingtime.adapters.RecipesAdapter;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromDb;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromInternet;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.LogUtils;
import com.lee.hansol.bakingtime.utils.UserStateUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Recipe[]> {
    private Unbinder butterKnifeUnbinder;
    private RecipesAdapter recipesAdapter;
    private final int LOADER_ID_LOAD_RECIPES = 111;

    @BindView(R.id.activity_main_recyclerview) RecyclerView mainRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butterKnifeUnbinder = ButterKnife.bind(this);
        LogUtils.context = this;

        initialize();
    }

    private void initialize() {
        initializeMainRecyclerView();
        loadRecipes();
    }

    private void initializeMainRecyclerView() {
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recipesAdapter = new RecipesAdapter(this);
        mainRecyclerView.setAdapter(recipesAdapter);
    }

    private void loadRecipes() {
        Loader<Recipe[]> loader = getSupportLoaderManager().getLoader(LOADER_ID_LOAD_RECIPES);
        if (loader == null) getSupportLoaderManager().initLoader(LOADER_ID_LOAD_RECIPES, null, this);
        else getSupportLoaderManager().restartLoader(LOADER_ID_LOAD_RECIPES, null, this);
    }

    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        if (UserStateUtils.hasInternetConnection(this))
            return new RecipesLoaderFromInternet(this);
        else
            return new RecipesLoaderFromDb(this);
    }

    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, @NonNull Recipe[] recipes) {
        log(String.format(Locale.getDefault(),
                getString(R.string.log_number_of_recipes_loaded_placeholder),
                recipes.length));
        recipesAdapter.setRecipes(recipes);
    }

    @Override
    public void onLoaderReset(Loader<Recipe[]> loader) {
        loader.cancelLoad();
    }

    @OnClick(R.id.testButton)
    public void testButton() {
        loadRecipes();
    }

    @Override
    protected void onDestroy() {
        LogUtils.context = null;
        butterKnifeUnbinder.unbind();
        super.onDestroy();
    }
}
