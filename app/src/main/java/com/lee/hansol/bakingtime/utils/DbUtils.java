package com.lee.hansol.bakingtime.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.lee.hansol.bakingtime.db.BakingProvider;
import com.lee.hansol.bakingtime.db.IngredientColumns;
import com.lee.hansol.bakingtime.db.RecipeColumns;
import com.lee.hansol.bakingtime.db.StepColumns;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import java.util.ArrayList;

import static com.lee.hansol.bakingtime.utils.DataUtils.emptyIngredients;
import static com.lee.hansol.bakingtime.utils.DataUtils.emptyRecipes;
import static com.lee.hansol.bakingtime.utils.DataUtils.emptySteps;

class DbUtils {

    static Cursor getRecipesTableCursor(Context context) {
        return context.getContentResolver().query(BakingProvider.Recipes.CONTENT_URI,
                null, null, null, null);
    }

    static Recipe[] getRecipesAndClose(Context context, @NonNull Cursor cursor) {
        Recipe[] recipes = getRecipesWith(context, cursor);
        cursor.close();
        return recipes;
    }

    private static Recipe[] getRecipesWith(Context context, @NonNull Cursor cursor) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                recipes.add(getRecipeObjectFrom(context, cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes.toArray(emptyRecipes);
    }

    private static Recipe getRecipeObjectFrom(Context context, @NonNull Cursor cursor) {
        int recipeId = cursor.getInt(cursor.getColumnIndex(RecipeColumns.RECIPE_ID));
        String recipeName = cursor.getString(cursor.getColumnIndex(RecipeColumns.NAME));
        Ingredient[] ingredients = getIngredientsOf(context, recipeId);
        Step[] steps = getStepsOf(context, recipeId);
        int servings = cursor.getInt(cursor.getColumnIndex(RecipeColumns.SERVINGS));
        String recipeImageUrlString = cursor.getString(cursor.getColumnIndex(RecipeColumns.IMAGE_URL));

        return new Recipe( recipeId, recipeName, ingredients, steps, servings, recipeImageUrlString);
    }

    private static Ingredient[] getIngredientsOf(Context context, int recipeId) {
        Cursor cursor = getIngredientsTableCursorOf(context, recipeId);
        if (cursor != null) {
            return getIngredientsAndClose(cursor, recipeId);
        } else {
            return emptyIngredients;
        }
    }

    private static Cursor getIngredientsTableCursorOf(Context context, int recipeId) {
        return context.getContentResolver().query(BakingProvider.Ingredients.CONTENT_URI,
                null, IngredientColumns.RECIPE_ID + " = ? ",
                new String[]{String.valueOf(recipeId)}, null);
    }

    private static Ingredient[] getIngredientsAndClose(@NonNull Cursor cursor, int recipeId) {
        Ingredient[] ingredients = getIngredientsWith(cursor, recipeId);
        cursor.close();
        return ingredients;
    }

    private static Ingredient[] getIngredientsWith(@NonNull Cursor cursor, int recipeId) {
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

    private static Ingredient getIngredientObjectFrom(@NonNull Cursor cursor, int recipeId) {
        String ingredientName = cursor.getString( cursor.getColumnIndex(IngredientColumns.NAME) );
        int quantity = cursor.getInt( cursor.getColumnIndex(IngredientColumns.QUANTITY) );
        String measureUnit = cursor.getString( cursor.getColumnIndex(IngredientColumns.MEASURE_UNIT) );
        return new Ingredient(recipeId, ingredientName, quantity, measureUnit);
    }

    private static Step[] getStepsOf(Context context, int recipeId) {
        Cursor cursor = getStepsTableCursorOf(context, recipeId);
        if (cursor != null)
            return getStepsAndClose(cursor, recipeId);
        else
            return emptySteps;
    }

    private static Cursor getStepsTableCursorOf(Context context, int recipeId) {
        return context.getContentResolver().query(BakingProvider.Steps.CONTENT_URI,
                null, StepColumns.RECIPE_ID + " = ? ",
                new String[]{String.valueOf(recipeId)}, StepColumns.STEP_ORDER);
    }

    private static Step[] getStepsAndClose(@NonNull Cursor cursor, int recipeId) {
        Step[] steps = getStepsWith(cursor, recipeId);
        cursor.close();
        return steps;
    }

    private static Step[] getStepsWith(@NonNull Cursor cursor, int recipeId) {
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

    private static Step getStepObjectFrom(@NonNull Cursor cursor, int recipeId) {
        int stepOrder = cursor.getInt( cursor.getColumnIndex(StepColumns.STEP_ORDER) );
        String shortDescription = cursor.getString( cursor.getColumnIndex(StepColumns.SHORT_DESCRIPTION) );
        String description = cursor.getString( cursor.getColumnIndex(StepColumns.DESCRIPTION) );
        String videoUrlString = cursor.getString( cursor.getColumnIndex(StepColumns.VIDEO_URL) );
        String thumbnailUrlString = cursor.getString( cursor.getColumnIndex(StepColumns.THUMBNAIL_URL) );
        return new Step(recipeId, stepOrder, shortDescription,
                description, videoUrlString, thumbnailUrlString);
    }

    static int saveAll(Context context, Recipe[] recipes, Ingredient[] ingredients, Step[] steps) {
        ContentValues[] recipeValues = getRecipeContentValues(recipes);
        ContentValues[] ingredientValues = getIngredientContentValues(ingredients);
        ContentValues[] stepValues = getStepContentValues(steps);
        int insertedCount = 0;
        insertedCount += context.getContentResolver().bulkInsert(BakingProvider.Recipes.CONTENT_URI, recipeValues);
        insertedCount += context.getContentResolver().bulkInsert(BakingProvider.Ingredients.CONTENT_URI, ingredientValues);
        insertedCount += context.getContentResolver().bulkInsert(BakingProvider.Steps.CONTENT_URI, stepValues);
        return insertedCount;
    }

    private static ContentValues[] getRecipeContentValues(Recipe[] recipes) {
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

    private static ContentValues[] getIngredientContentValues(Ingredient[] ingredients) {
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

    private static ContentValues[] getStepContentValues(Step[] steps) {
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

}
