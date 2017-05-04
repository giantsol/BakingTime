package com.lee.hansol.bakingtime.loaders;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipesLoaderFromInternet extends AsyncTaskLoader<Recipe[]> {
    private Recipe[] recipes;
    private final Recipe[] emptyRecipes = new Recipe[0];
    private int currentRecipeId;

    public RecipesLoaderFromInternet(Context context) { super(context); }

    @Override
    protected void onStartLoading() {
        log(getContext().getString(R.string.log_on_start_loading_from_internet_loader));
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        log(getContext().getString(R.string.log_load_in_background_from_internet_loader));
        String urlString = getContext().getString(R.string.url_baking_recipe);
        try {
            Recipe[] recipes = getRecipesFromUrl(urlString);
            saveRecipesToDb(recipes);
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
            return getRecipeObjectsFromJsonArray(recipesJsonArray);
        } else {
            return emptyRecipes;
        }
    }

    private Recipe[] getRecipeObjectsFromJsonArray(@NonNull JSONArray recipesJsonArray) throws Exception {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < recipesJsonArray.length(); i++) {
            JSONObject recipeJson = recipesJsonArray.getJSONObject(i);
            Recipe recipe = getRecipeObjectFromJsonObject(recipeJson);
            recipes.add(recipe);
        }
        return recipes.toArray(emptyRecipes);
    }

    private Recipe getRecipeObjectFromJsonObject(JSONObject recipeJson) throws Exception {
        currentRecipeId = recipeJson.getInt("id");
        String recipeName = recipeJson.getString("name");
        Ingredient[] ingredients = getIngredientObjectsFromJsonArray( recipeJson.getJSONArray("ingredients") );
        Step[] steps = getStepObjectsFromJsonArray( recipeJson.getJSONArray("steps") );
        int servings = recipeJson.getInt("servings");
        String imageUrlString = recipeJson.getString("image");
        return new Recipe(currentRecipeId, recipeName, ingredients, steps, servings, imageUrlString);
    }

    private Ingredient[] getIngredientObjectsFromJsonArray(JSONArray ingredientsJsonArray) throws Exception{
        ArrayList<Ingredient> ingredients = new ArrayList<>(ingredientsJsonArray.length());
        for (int j = 0; j < ingredientsJsonArray.length(); j++) {
            JSONObject ingredientJson = ingredientsJsonArray.getJSONObject(j);
            Ingredient ingredient = getIngredientObjectFromJsonObject(ingredientJson);
            ingredients.add(ingredient);
        }
        return ingredients.toArray(new Ingredient[0]);
    }

    private Ingredient getIngredientObjectFromJsonObject(JSONObject ingredientJson) throws Exception{
        int quantity = ingredientJson.getInt("quantity");
        String measureUnit = ingredientJson.getString("measure");
        String ingredientName = ingredientJson.getString("ingredient");
        return new Ingredient(currentRecipeId, ingredientName, quantity, measureUnit);
    }

    private Step[] getStepObjectsFromJsonArray(JSONArray stepsJsonArray) throws Exception{
        ArrayList<Step> steps = new ArrayList<>(stepsJsonArray.length());
        for (int j = 0; j < stepsJsonArray.length(); j++) {
            JSONObject stepJson = stepsJsonArray.getJSONObject(j);
            Step step = getStepObjectFromJsonObject(stepJson);
            steps.add(step);
        }
        return steps.toArray(new Step[0]);
    }

    private Step getStepObjectFromJsonObject(JSONObject stepJson) throws Exception{
        int stepOrder = stepJson.getInt("id");
        String shortDescription = stepJson.getString("shortDescription");
        String description = stepJson.getString("description");
        String videoUrlString = stepJson.getString("videoURL");
        String thumbnailUrlString = stepJson.getString("thumbnailURL");
        return new Step(currentRecipeId, stepOrder, shortDescription, description,
                videoUrlString, thumbnailUrlString);
    }

    private int saveRecipesToDb(Recipe[] updatedRecipes) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ArrayList<Step> steps = new ArrayList<>();
        for (Recipe recipe : updatedRecipes) {
            Collections.addAll(ingredients, recipe.ingredients);
            Collections.addAll(steps, recipe.steps);
        }

        ContentValues[] recipeValues = getRecipeContentValues(updatedRecipes);
        ContentValues[] ingredientValues = getIngredientContentValues(ingredients.toArray(new Ingredient[0]));
        ContentValues[] stepValues = getStepContentValues(steps.toArray(new Step[0]));
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
        Log.d("hello", "deliverresult from internet thing");
        recipes = data;
        super.deliverResult(data);
    }
}
