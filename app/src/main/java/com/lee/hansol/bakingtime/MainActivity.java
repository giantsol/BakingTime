package com.lee.hansol.bakingtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lee.hansol.bakingtime.models.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Recipe[]> {
    private Unbinder butterKnifeUnbinder;
    private RecipesAdapter recipesAdapter;

    @BindView(R.id.activity_main_recyclerview)
    private RecyclerView mainRecyclerView;

    private static int LOADER_ID_LOAD_RECIPES_FROM_INTERNET = 111;
    private static int LOADER_ID_LOAD_RECIPES_FROM_DB = 112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butterKnifeUnbinder = ButterKnife.bind(this);
        loadRecipes();
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recipesAdapter = new RecipesAdapter(this);
        mainRecyclerView.setAdapter(recipesAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID_LOAD_RECIPES_FROM_INTERNET, null, this);
    }

    private void loadRecipes() {
        if (hasInternetConnection()) {
            Loader<Recipe[]> loader = getSupportLoaderManager().getLoader(LOADER_ID_LOAD_RECIPES_FROM_INTERNET);
            if (loader == null) getSupportLoaderManager().initLoader(LOADER_ID_LOAD_RECIPES_FROM_INTERNET, null, this);
            else getSupportLoaderManager().restartLoader(LOADER_ID_LOAD_RECIPES_FROM_INTERNET, null, this);
        } else {
            Loader<Recipe[]> loader = getSupportLoaderManager().getLoader(LOADER_ID_LOAD_RECIPES_FROM_DB);
            if (loader == null) getSupportLoaderManager().initLoader(LOADER_ID_LOAD_RECIPES_FROM_DB, null, this);
            else getSupportLoaderManager().restartLoader(LOADER_ID_LOAD_RECIPES_FROM_DB, null, this);
        }
    }

    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, Recipe[] data) {

    }

    @Override
    public void onLoaderReset(Loader<Recipe[]> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        butterKnifeUnbinder.unbind();
    }
}
