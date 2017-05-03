package com.lee.hansol.bakingtime.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.lee.hansol.bakingtime.db.BakingProvider;
import com.lee.hansol.bakingtime.db.IngredientColumns;
import com.lee.hansol.bakingtime.db.RecipeColumns;
import com.lee.hansol.bakingtime.db.StepColumns;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import java.util.ArrayList;

public class RecipesLoaderFromDb extends AsyncTaskLoader<Recipe[]> {
    private Recipe[] recipes;

    public RecipesLoaderFromDb(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        Cursor cursor = null;
        ArrayList<Recipe> recipes = new ArrayList<>();

        try {
            cursor = getContext().getContentResolver().query(BakingProvider.Recipes.CONTENT_URI,
                    null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int recipeId = cursor.getInt(cursor.getColumnIndex(RecipeColumns.RECIPE_ID));
                    String recipeName = cursor.getString(cursor.getColumnIndex(RecipeColumns.NAME));
                    int servings = cursor.getInt(cursor.getColumnIndex(RecipeColumns.SERVINGS));
                    String recipeImageUrlString = cursor.getString(cursor.getColumnIndex(RecipeColumns.IMAGE_URL));
                    ArrayList<Ingredient> ingredients = new ArrayList<>();
                    Cursor ingredientCursor =
                            getContext().getContentResolver().query(BakingProvider.Ingredients.CONTENT_URI,
                                    null, IngredientColumns.RECIPE_ID + " = ? ",
                                    new String[]{String.valueOf(recipeId)}, null);
                    if (ingredientCursor != null) {
                        while (ingredientCursor.moveToNext()) {
                            String ingredientName = ingredientCursor.getString(
                                    ingredientCursor.getColumnIndex(IngredientColumns.NAME)
                            );
                            int quantity = ingredientCursor.getInt(
                                    ingredientCursor.getColumnIndex(IngredientColumns.QUANTITY)
                            );
                            String measureUnit = ingredientCursor.getString(
                                    ingredientCursor.getColumnIndex(IngredientColumns.MEASURE_UNIT)
                            );
                            Ingredient ingredient = new Ingredient(recipeId, ingredientName, quantity, measureUnit);
                            ingredients.add(ingredient);
                        }
                        ingredientCursor.close();
                    }
                    ArrayList<Step> steps = new ArrayList<>();
                    Cursor stepCursor =
                            getContext().getContentResolver().query(BakingProvider.Steps.CONTENT_URI,
                                    null, StepColumns.RECIPE_ID + " = ? ",
                                    new String[]{String.valueOf(recipeId)},
                                    StepColumns.STEP_ORDER);
                    if (stepCursor != null) {
                        while (stepCursor.moveToNext()) {
                            int stepOrder = stepCursor.getInt(
                                    stepCursor.getColumnIndex(StepColumns.STEP_ORDER)
                            );
                            String shortDescription = stepCursor.getString(
                                    stepCursor.getColumnIndex(StepColumns.SHORT_DESCRIPTION)
                            );
                            String description = stepCursor.getString(
                                    stepCursor.getColumnIndex(StepColumns.DESCRIPTION)
                            );
                            String videoUrlString = stepCursor.getString(
                                    stepCursor.getColumnIndex(StepColumns.VIDEO_URL)
                            );
                            String thumbnailUrlString = stepCursor.getString(
                                    stepCursor.getColumnIndex(StepColumns.THUMBNAIL_URL)
                            );
                            Step step = new Step(recipeId, stepOrder, shortDescription,
                                    description, videoUrlString, thumbnailUrlString);
                            steps.add(step);
                        }
                        stepCursor.close();
                    }
                    Recipe recipe = new Recipe(
                            recipeId, recipeName, ingredients.toArray(new Ingredient[0]),
                            steps.toArray(new Step[0]), servings, recipeImageUrlString
                    );
                    recipes.add(recipe);
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return recipes.toArray(new Recipe[0]);
    }

    @Override
    public void deliverResult(Recipe[] data) {
        recipes = data;
        super.deliverResult(data);
    }
}
