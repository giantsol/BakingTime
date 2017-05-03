package com.lee.hansol.bakingtime;

import android.content.ContentValues;
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
import com.lee.hansol.bakingtime.db.BakingProvider;
import com.lee.hansol.bakingtime.db.IngredientColumns;
import com.lee.hansol.bakingtime.db.RecipeColumns;
import com.lee.hansol.bakingtime.db.StepColumns;
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromInternet;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;
import com.lee.hansol.bakingtime.utils.ExceptionUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
//                return new RecipesLoaderFromDb(this);
                return new RecipesLoaderFromInternet(this);
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
        } else {
            toast(this, "recipes length: " + data.length);
        }

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ArrayList<Step> steps = new ArrayList<>();
        for (Recipe recipe : data) {
            Collections.addAll(ingredients, recipe.ingredients);
            Collections.addAll(steps, recipe.steps);
        }

        ContentValues[] recipeValues = getRecipeContentValues(data);
        ContentValues[] ingredientValues = getIngredientContentValues(ingredients.toArray(new Ingredient[0]));
        ContentValues[] stepValues = getStepContentValues(steps.toArray(new Step[0]));
        int insertedCount = 0;
        insertedCount += getContentResolver().bulkInsert(BakingProvider.Recipes.CONTENT_URI, recipeValues);
        insertedCount += getContentResolver().bulkInsert(BakingProvider.Ingredients.CONTENT_URI, ingredientValues);
        insertedCount += getContentResolver().bulkInsert(BakingProvider.Steps.CONTENT_URI, stepValues);
        toast(this, "" + insertedCount);
    }

    private ContentValues[] getRecipeContentValues(Recipe[] recipes) {
        ArrayList<ContentValues> values = new ArrayList<>(recipes.length);
        for (Recipe recipe : recipes) {
            ContentValues value = new ContentValues();
            value.put(RecipeColumns.RECIPE_ID, recipe.recipeId);
            value.put(RecipeColumns.NAME, recipe.name);
            value.put(RecipeColumns.SERVINGS, recipe.servings);
            value.put(RecipeColumns.IMAGE_URL, recipe.imageUrlString);
            values.add(value);
        }
        return values.toArray(new ContentValues[0]);
    }

    private ContentValues[] getIngredientContentValues(Ingredient[] ingredients) {
        ArrayList<ContentValues> values = new ArrayList<>(ingredients.length);
        for (Ingredient ingredient : ingredients) {
            ContentValues value = new ContentValues();
            value.put(IngredientColumns.RECIPE_ID, ingredient.recipeId);
            value.put(IngredientColumns.NAME, ingredient.name);
            value.put(IngredientColumns.QUANTITY, ingredient.quantity);
            value.put(IngredientColumns.MEASURE_UNIT, ingredient.measureUnit);
            values.add(value);
        }
        return values.toArray(new ContentValues[0]);
    }

    private ContentValues[] getStepContentValues(Step[] steps) {
        ArrayList<ContentValues> values = new ArrayList<>(steps.length);
        for (Step step : steps) {
            ContentValues value = new ContentValues();
            value.put(StepColumns.RECIPE_ID, step.recipeId);
            value.put(StepColumns.STEP_ORDER, step.stepOrder);
            value.put(StepColumns.SHORT_DESCRIPTION, step.shortDescription);
            value.put(StepColumns.DESCRIPTION, step.description);
            value.put(StepColumns.VIDEO_URL, step.videoUrlString);
            value.put(StepColumns.THUMBNAIL_URL, step.thumbnailUrlString);
            values.add(value);
        }
        return values.toArray(new ContentValues[0]);
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
