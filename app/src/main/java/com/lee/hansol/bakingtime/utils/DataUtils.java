package com.lee.hansol.bakingtime.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lee.hansol.bakingtime.db.BakingProvider;
import com.lee.hansol.bakingtime.db.RecipeColumns;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;

public class DataUtils {
    public static final Recipe[] emptyRecipes = new Recipe[0];
    static final Ingredient[] emptyIngredients = new Ingredient[0];
    static final Step[] emptySteps = new Step[0];

    public static Recipe[] loadRecipesFromDb(Context context) {
        Cursor cursor = DbUtils.getRecipesTableCursor(context);
        if (cursor != null) {
            return DbUtils.getRecipesAndClose(context, cursor);
        } else {
            return emptyRecipes;
        }
    }

    @NonNull
    public static Recipe[] loadRecipesFromUrl(Context context, String url) throws Exception {
        JSONArray recipesJsonArray = NetworkUtils.getJSONArrayFromUrl(context, url);
        if (recipesJsonArray != null) {
            return JsonUtils.getRecipeObjectsFromJsonArray(recipesJsonArray);
        } else {
            return emptyRecipes;
        }
    }

    public static int saveRecipesToDb(Context context, Recipe[] updatedRecipes) {
        Ingredient[] updatedIngredients = getAllIngredientsFrom(updatedRecipes);
        Step[] updatedSteps = getAllStepsFrom(updatedRecipes);
        return DbUtils.saveAll(context, updatedRecipes, updatedIngredients, updatedSteps);
    }

    private static Ingredient[] getAllIngredientsFrom(Recipe[] recipes) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (Recipe recipe : recipes) {
            Collections.addAll(ingredients, recipe.ingredients);
        }
        return ingredients.toArray(emptyIngredients);
    }

    private static Step[] getAllStepsFrom(Recipe[] recipes) {
        ArrayList<Step> steps = new ArrayList<>();
        for (Recipe recipe : recipes) {
            Collections.addAll(steps, recipe.steps);
        }
        return steps.toArray(emptySteps);
    }

    @Nullable
    public static Recipe getRecipeObjectWithRecipeId(Context context, int recipeId) {
        Cursor cursor = context.getContentResolver().query(BakingProvider.Recipes.CONTENT_URI,
                null, RecipeColumns.RECIPE_ID + " = ?", new String[]{recipeId + ""}, null);
        Recipe recipe = null;
        if (cursor != null) {
            cursor.moveToFirst();
            recipe = DbUtils.getRecipeObjectFrom(context, cursor);
            cursor.close();
        }
        return recipe;
    }
}
