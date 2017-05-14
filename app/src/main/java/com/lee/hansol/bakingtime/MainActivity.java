package com.lee.hansol.bakingtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lee.hansol.bakingtime.adapters.RecipesRecyclerViewAdapter;
import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromDb;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromInternet;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.LogUtils;
import com.lee.hansol.bakingtime.utils.User;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;
import static com.lee.hansol.bakingtime.utils.ToastUtils.isToastShowingWithMsg;
import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Recipe[]>,
        RecipesRecyclerViewAdapter.OnRecipeItemClickListener {
    private RecipesRecyclerViewAdapter recipesAdapter;

    private final int LOADER_ID_LOAD_RECIPES = 111;

    @BindView(R.id.activity_main_recyclerview) RecyclerView mainRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LogUtils.setContext(this);

        initialize();
    }

    private void initialize() {
        initializeMainRecyclerView();
        loadRecipes();
    }

    private void initializeMainRecyclerView() {
        mainRecyclerView.setHasFixedSize(true);
        int colNum = getResources().getInteger(R.integer.main_grid_column_number);
        GridLayoutManager grid = new GridLayoutManager(this, colNum, LinearLayoutManager.VERTICAL, false);
        mainRecyclerView.setLayoutManager(grid);
        recipesAdapter = new RecipesRecyclerViewAdapter(this, this);
        mainRecyclerView.setAdapter(recipesAdapter);
    }

    private void loadRecipes() {
        Loader<Recipe[]> loader = getSupportLoaderManager().getLoader(LOADER_ID_LOAD_RECIPES);
        if (loader == null) getSupportLoaderManager().initLoader(LOADER_ID_LOAD_RECIPES, null, this);
        else getSupportLoaderManager().restartLoader(LOADER_ID_LOAD_RECIPES, null, this);
    }

    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        if (User.hasInternetConnection(this))
            return new RecipesLoaderFromInternet(this);
        else
            return new RecipesLoaderFromDb(this);
    }

    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, @NonNull Recipe[] recipes) {
        log(String.format(Locale.getDefault(),
                getString(R.string.log_number_of_recipes_loaded_placeholder),
                recipes.length));
        DataHelper.getInstance().setRecipes(recipes);
        recipesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Recipe[]> loader) {
        loader.cancelLoad();
    }

    @Override
    public void onRecipeItemClick(int recipeIndex) {
        DataHelper.getInstance().setCurrentRecipeIndex(recipeIndex);
        startRecipeDetailActivity();
    }

    private void startRecipeDetailActivity() {
        startActivity(new Intent(this, RecipeDetailActivity.class));
    }

    @Override
    public void onBackPressed() {
        String quitString = getString(R.string.toast_really_exit_app);
        if (isToastShowingWithMsg(quitString)) super.onBackPressed();
        else toast(this, quitString);
    }

    @Override
    protected void onDestroy() {
        LogUtils.setContext(null);
        super.onDestroy();
    }
}
