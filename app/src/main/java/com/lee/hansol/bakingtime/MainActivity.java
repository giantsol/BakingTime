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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lee.hansol.bakingtime.adapters.RecipesRecyclerViewAdapter;
import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromDb;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromInternet;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.LogUtils;
import com.lee.hansol.bakingtime.utils.User;

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
    @BindView(R.id.activity_main_progressbar) ProgressBar progressBar;
    @BindView(R.id.activity_main_error_textview) TextView errorTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        initializeViewVisibilities();
        initializeMainRecyclerView();
        startLoaderForRecipes();
    }

    private void initializeViewVisibilities() {
        mainRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
    }

    private void initializeMainRecyclerView() {
        mainRecyclerView.setHasFixedSize(true);
        int colNum = getResources().getInteger(R.integer.main_grid_column_number);
        GridLayoutManager grid = new GridLayoutManager(this, colNum, LinearLayoutManager.VERTICAL, false);
        mainRecyclerView.setLayoutManager(grid);
        recipesAdapter = new RecipesRecyclerViewAdapter(this, this);
        mainRecyclerView.setAdapter(recipesAdapter);
    }

    private void startLoaderForRecipes() {
        showOnlyProgressBar();
        Loader<Recipe[]> loader = getSupportLoaderManager().getLoader(LOADER_ID_LOAD_RECIPES);
        if (loader == null) getSupportLoaderManager().initLoader(LOADER_ID_LOAD_RECIPES, null, this);
        else getSupportLoaderManager().restartLoader(LOADER_ID_LOAD_RECIPES, null, this);
    }

    private void showOnlyProgressBar() {
        mainRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
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
        if (recipes.length == 0)
            if (loader instanceof RecipesLoaderFromInternet)
                showErrorWhileLoadingFromInternet();
            else
                showErrorWhileLoadingFromDb();
        else {
            DataStorage.getInstance().setRecipes(recipes);
            recipesAdapter.notifyDataSetChanged();
            showRecipesView();
        }
    }

    private void showErrorWhileLoadingFromInternet() {
        mainRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(getString(R.string.text_error_loading_from_internet));
    }

    private void showErrorWhileLoadingFromDb() {
        mainRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(getString(R.string.text_error_loading_from_db));
    }

    private void showRecipesView() {
        mainRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Recipe[]> loader) {
        loader.cancelLoad();
    }

    @Override
    public void onRecipeItemClick(int recipeIndex) {
        DataStorage.getInstance().setCurrentRecipeIndex(recipeIndex);
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
    protected void onStart() {
        super.onStart();
        LogUtils.setContext(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) LogUtils.setContext(null);
    }
}
