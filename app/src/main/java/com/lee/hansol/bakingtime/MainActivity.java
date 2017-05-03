package com.lee.hansol.bakingtime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lee.hansol.bakingtime.adapters.RecipesAdapter;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromDb;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.ExceptionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.R.attr.data;
import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Recipe[]> {
    private Unbinder butterKnifeUnbinder;
    private RecipesAdapter recipesAdapter;
    private boolean loadRecipesFromInternet = false;

    @BindView(R.id.activity_main_recyclerview)
    RecyclerView mainRecyclerView;

    private final int LOADER_ID_LOAD_RECIPES = 111;

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
    }

    private void loadRecipes() {
        Loader<Recipe[]> loader = getSupportLoaderManager().getLoader(LOADER_ID_LOAD_RECIPES);
        if (loader == null) getSupportLoaderManager().initLoader(LOADER_ID_LOAD_RECIPES, null, this);
        else getSupportLoaderManager().restartLoader(LOADER_ID_LOAD_RECIPES, null, this);
    }

    private boolean hasInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_LOAD_RECIPES:
                return new RecipesLoaderFromDb(this);
            default:
                ExceptionUtils.throwUnknownIdException(this, id);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, @NonNull Recipe[] data) {
        recipesAdapter.setRecipes(data);

        //debug
        if (data.length == 0) {
            toast(this, "empty recipes~~");
        }
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
        super.onDestroy();
        butterKnifeUnbinder.unbind();
    }
}
