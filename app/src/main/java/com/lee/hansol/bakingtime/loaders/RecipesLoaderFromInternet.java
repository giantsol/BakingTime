package com.lee.hansol.bakingtime.loaders;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.db.BakingProvider;
import com.lee.hansol.bakingtime.db.IngredientColumns;
import com.lee.hansol.bakingtime.db.RecipeColumns;
import com.lee.hansol.bakingtime.db.StepColumns;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;
import com.lee.hansol.bakingtime.utils.JsonUtils;
import com.lee.hansol.bakingtime.utils.NetworkUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipesLoaderFromInternet extends AsyncTaskLoader<Recipe[]> {
    private Recipe[] recipes;
    private final Recipe[] emptyRecipes = new Recipe[0];

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
            Recipe[] recipes = getRecipesFromUrl(urlString);
            int insertedRowNum = saveRecipesToDb(recipes);
            log(String.format(Locale.getDefault(),
                    getContext().getString(R.string.log_inserted_row_count_placeholder),
                    insertedRowNum));
            return recipes;
        } catch (Exception e) {
            e.printStackTrace();
            return emptyRecipes;
        }
    }

    @NonNull
    private Recipe[] getRecipesFromUrl(String url) throws Exception {
        JSONArray recipesJsonArray = NetworkUtils.getJSONArrayFromUrl(getContext(), url);
        if (recipesJsonArray != null) {
            return JsonUtils.getRecipeObjectsFromJsonArray(recipesJsonArray);
        } else {
            return emptyRecipes;
        }
    }

    private int saveRecipesToDb(Recipe[] updatedRecipes) {
        Ingredient[] updatedIngredients = getAllIngredientsFrom(updatedRecipes);
        Step[] updatedSteps = getAllStepsFrom(updatedRecipes);
        return saveAndReturnInsertedRowNum(updatedRecipes, updatedIngredients, updatedSteps);
    }

    private Ingredient[] getAllIngredientsFrom(Recipe[] recipes) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (Recipe recipe : recipes) {
            Collections.addAll(ingredients, recipe.ingredients);
        }
        return ingredients.toArray(new Ingredient[0]);
    }

    private Step[] getAllStepsFrom(Recipe[] recipes) {
        ArrayList<Step> steps = new ArrayList<>();
        for (Recipe recipe : recipes) {
            Collections.addAll(steps, recipe.steps);
        }
        return steps.toArray(new Step[0]);
    }

    private int saveAndReturnInsertedRowNum(Recipe[] recipes, Ingredient[] ingredients, Step[] steps) {
        ContentValues[] recipeValues = getRecipeContentValues(recipes);
        ContentValues[] ingredientValues = getIngredientContentValues(ingredients);
        ContentValues[] stepValues = getStepContentValues(steps);
        int insertedCount = 0;
        insertedCount += getContext().getContentResolver().bulkInsert(BakingProvider.Recipes.CONTENT_URI, recipeValues);
        insertedCount += getContext().getContentResolver().bulkInsert(BakingProvider.Ingredients.CONTENT_URI, ingredientValues);
        insertedCount += getContext().getContentResolver().bulkInsert(BakingProvider.Steps.CONTENT_URI, stepValues);
        return insertedCount;
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
    public void deliverResult(@NonNull Recipe[] data) {
        log(R.string.log_deliver_result_from_internet_loader);
        recipes = data;
        super.deliverResult(data);
    }
}
