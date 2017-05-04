package com.lee.hansol.bakingtime.loaders;

import android.content.Context;
import android.database.Cursor;
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

import java.util.ArrayList;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipesLoaderFromDb extends AsyncTaskLoader<Recipe[]> {
    private Recipe[] recipes;
    private final Recipe[] emptyRecipes = new Recipe[0];
    private final Ingredient[] emptyIngredients = new Ingredient[0];
    private final Step[] emptySteps = new Step[0];

    public RecipesLoaderFromDb(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        log(getContext().getString(R.string.log_on_start_loading_from_db_loader));
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        log(getContext().getString(R.string.log_load_in_background_from_db_loader));
        Cursor cursor = getRecipesTableCursor();
        if (cursor != null) {
            return getRecipesAndClose(cursor);
        } else {
            return emptyRecipes;
        }
    }

    private Cursor getRecipesTableCursor() {
        return getContext().getContentResolver().query(BakingProvider.Recipes.CONTENT_URI,
                        null, null, null, null);
    }

    private Recipe[] getRecipesAndClose(@NonNull Cursor cursor) {
        Recipe[] recipes = getRecipesWith(cursor);
        cursor.close();
        return recipes;
    }

    private Recipe[] getRecipesWith(@NonNull Cursor cursor) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                recipes.add(getRecipeObjectFrom(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes.toArray(emptyRecipes);
    }

    private Recipe getRecipeObjectFrom(@NonNull Cursor cursor) {
        int recipeId = cursor.getInt(cursor.getColumnIndex(RecipeColumns.RECIPE_ID));
        String recipeName = cursor.getString(cursor.getColumnIndex(RecipeColumns.NAME));
        Ingredient[] ingredients = getIngredientsOf(recipeId);
        Step[] steps = getStepsOf(recipeId);
        int servings = cursor.getInt(cursor.getColumnIndex(RecipeColumns.SERVINGS));
        String recipeImageUrlString = cursor.getString(cursor.getColumnIndex(RecipeColumns.IMAGE_URL));

        return new Recipe( recipeId, recipeName, ingredients, steps, servings, recipeImageUrlString);
    }

    private Ingredient[] getIngredientsOf(int recipeId) {
        Cursor cursor = getIngredientsTableCursorOf(recipeId);
        if (cursor != null) {
            return getIngredientsAndClose(cursor, recipeId);
        } else {
            return emptyIngredients;
        }
    }

    private Cursor getIngredientsTableCursorOf(int recipeId) {
        return getContext().getContentResolver().query(BakingProvider.Ingredients.CONTENT_URI,
                        null, IngredientColumns.RECIPE_ID + " = ? ",
                        new String[]{String.valueOf(recipeId)}, null);
    }

    private Ingredient[] getIngredientsAndClose(@NonNull Cursor cursor, int recipeId) {
        Ingredient[] ingredients = getIngredientsWith(cursor, recipeId);
        cursor.close();
        return ingredients;
    }

    private Ingredient[] getIngredientsWith(@NonNull Cursor cursor, int recipeId) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                ingredients.add(getIngredientObjectFrom(cursor, recipeId));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ingredients.toArray(emptyIngredients);
    }

    private Ingredient getIngredientObjectFrom(@NonNull Cursor cursor, int recipeId) {
        String ingredientName = cursor.getString( cursor.getColumnIndex(IngredientColumns.NAME) );
        int quantity = cursor.getInt( cursor.getColumnIndex(IngredientColumns.QUANTITY) );
        String measureUnit = cursor.getString( cursor.getColumnIndex(IngredientColumns.MEASURE_UNIT) );
        return new Ingredient(recipeId, ingredientName, quantity, measureUnit);
    }

    private Step[] getStepsOf(int recipeId) {
        Cursor cursor = getStepsTableCursorOf(recipeId);
        if (cursor != null)
            return getStepsAndClose(cursor, recipeId);
        else
            return emptySteps;
    }

    private Cursor getStepsTableCursorOf(int recipeId) {
        return getContext().getContentResolver().query(BakingProvider.Steps.CONTENT_URI,
                        null, StepColumns.RECIPE_ID + " = ? ",
                        new String[]{String.valueOf(recipeId)}, StepColumns.STEP_ORDER);
    }

    private Step[] getStepsAndClose(@NonNull Cursor cursor, int recipeId) {
        Step[] steps = getStepsWith(cursor, recipeId);
        cursor.close();
        return steps;
    }

    private Step[] getStepsWith(@NonNull Cursor cursor, int recipeId) {
        ArrayList<Step> steps = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                steps.add(getStepObjectFrom(cursor, recipeId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps.toArray(emptySteps);
    }

    private Step getStepObjectFrom(@NonNull Cursor cursor, int recipeId) {
        int stepOrder = cursor.getInt( cursor.getColumnIndex(StepColumns.STEP_ORDER) );
        String shortDescription = cursor.getString( cursor.getColumnIndex(StepColumns.SHORT_DESCRIPTION) );
        String description = cursor.getString( cursor.getColumnIndex(StepColumns.DESCRIPTION) );
        String videoUrlString = cursor.getString( cursor.getColumnIndex(StepColumns.VIDEO_URL) );
        String thumbnailUrlString = cursor.getString( cursor.getColumnIndex(StepColumns.THUMBNAIL_URL) );
        return new Step(recipeId, stepOrder, shortDescription,
                description, videoUrlString, thumbnailUrlString);
    }

    @Override
    public void deliverResult(@NonNull Recipe[] data) {
        log(getContext().getString(R.string.log_deliver_result_from_db_loader));
        recipes = data;
        super.deliverResult(data);
    }
}
