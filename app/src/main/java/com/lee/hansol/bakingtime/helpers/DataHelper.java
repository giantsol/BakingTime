package com.lee.hansol.bakingtime.helpers;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lee.hansol.bakingtime.db.BakingProvider;
import com.lee.hansol.bakingtime.db.IngredientColumns;
import com.lee.hansol.bakingtime.db.RecipeColumns;
import com.lee.hansol.bakingtime.db.StepColumns;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import java.util.ArrayList;

public final class DataHelper {
    private static DataHelper singletonSelf;
    public static DataHelper getInstance() {
        if (singletonSelf == null) singletonSelf = new DataHelper();
        return singletonSelf;
    }
    private DataHelper(){}

    @NonNull private Recipe[] recipes = new Recipe[0];
    @NonNull private Ingredient[] ingredients = new Ingredient[0];
    @NonNull private Step[] steps = new Step[0];
    private int currentRecipeIndex = -1;
    private int currentStepIndex = -1;
    @Nullable private Recipe currentRecipeObject = null;
    @Nullable private Step currentStepObject = null;
    private final Recipe[] emptyRecipes = new Recipe[0];
    private final Ingredient[] emptyIngredients = new Ingredient[0];
    private final Step[] emptySteps = new Step[0];

    //Recipes

    public void setRecipes(@NonNull Recipe[] recipes) { this.recipes = recipes; }

    public void setCurrentRecipeIndex(int index) {
        if (index >= recipes.length || index < 0)
            throw new RuntimeException("invalid index: " + index);
        currentRecipeIndex = index;
        currentRecipeObject = recipes[currentRecipeIndex];
        ingredients = currentRecipeObject.ingredients;
        steps = currentRecipeObject.steps;
    }

    @Nullable
    public Recipe getRecipeObjectAt(int index) {
        if (index >= recipes.length || index < 0) return null;
        else return recipes[index];
    }

    public Recipe[] getAllRecipes() { return recipes; }

    @Nullable
    public Recipe getCurrentRecipeObject() { return currentRecipeObject; }

    public int getCurrentRecipeIndex() { return currentRecipeIndex; }

    //Steps

    public void setCurrentStepIndex(int index) {
        if (index >= steps.length || index < 0)
            throw new RuntimeException("invalid index: " + index);
        currentStepIndex = index;
        currentStepObject = steps[currentStepIndex];
    }

    @Nullable
    public Step getStepObjectAt(int index) {
        if (index >= steps.length || index < 0) return null;
        else return steps[index];
    }

    public Step[] getAllSteps() { return steps; }

    @Nullable
    public Step getCurrentStepObject() { return currentStepObject; }

    public int getCurrentStepIndex() { return currentStepIndex; }

    public boolean hasNextStep() {
        return currentStepIndex < steps.length - 1;
    }

    public boolean hasPreviousStep() {
        return (steps.length > 0) && (currentStepIndex >= 1);
    }

    public void moveToNextStep() {
        setCurrentStepIndex(currentStepIndex + 1);
    }

    public void moveToPreviousStep() {
        setCurrentStepIndex(currentStepIndex - 1);
    }

    //Ingredients

    @Nullable
    public Ingredient getIngredientObjectAt(int index) {
        if (index >= ingredients.length || index < 0) return null;
        else return ingredients[index];
    }

    public Ingredient[] getAllIngredients() { return ingredients; }

    //

    public Recipe[] loadRecipesFromDb(Context context) {
        Cursor cursor = getRecipesTableCursor(context);
        if (cursor != null) {
            return getRecipesAndClose(context, cursor);
        } else {
            return emptyRecipes;
        }
    }

    private Cursor getRecipesTableCursor(Context context) {
        return context.getContentResolver().query(BakingProvider.Recipes.CONTENT_URI,
                null, null, null, null);
    }

    private Recipe[] getRecipesAndClose(Context context, @NonNull Cursor cursor) {
        Recipe[] recipes = getRecipesWith(context, cursor);
        cursor.close();
        return recipes;
    }

    private Recipe[] getRecipesWith(Context context, @NonNull Cursor cursor) {
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

    private Recipe getRecipeObjectFrom(Context context, @NonNull Cursor cursor) {
        int recipeId = cursor.getInt(cursor.getColumnIndex(RecipeColumns.RECIPE_ID));
        String recipeName = cursor.getString(cursor.getColumnIndex(RecipeColumns.NAME));
        Ingredient[] ingredients = getIngredientsOf(context, recipeId);
        Step[] steps = getStepsOf(context, recipeId);
        int servings = cursor.getInt(cursor.getColumnIndex(RecipeColumns.SERVINGS));
        String recipeImageUrlString = cursor.getString(cursor.getColumnIndex(RecipeColumns.IMAGE_URL));

        return new Recipe( recipeId, recipeName, ingredients, steps, servings, recipeImageUrlString);
    }

    private Ingredient[] getIngredientsOf(Context context, int recipeId) {
        Cursor cursor = getIngredientsTableCursorOf(context, recipeId);
        if (cursor != null) {
            return getIngredientsAndClose(cursor, recipeId);
        } else {
            return emptyIngredients;
        }
    }

    private Cursor getIngredientsTableCursorOf(Context context, int recipeId) {
        return context.getContentResolver().query(BakingProvider.Ingredients.CONTENT_URI,
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

    private Step[] getStepsOf(Context context, int recipeId) {
        Cursor cursor = getStepsTableCursorOf(context, recipeId);
        if (cursor != null)
            return getStepsAndClose(cursor, recipeId);
        else
            return emptySteps;
    }

    private Cursor getStepsTableCursorOf(Context context, int recipeId) {
        return context.getContentResolver().query(BakingProvider.Steps.CONTENT_URI,
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
}

