package com.lee.hansol.bakingtime.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static Recipe[] getRecipeObjectsFromJsonArray(@NonNull JSONArray recipesJsonArray) {
        ArrayList<Recipe> recipes = new ArrayList<>(recipesJsonArray.length());
        try {
            for (int i = 0; i < recipesJsonArray.length(); i++) {
                JSONObject recipeJson = recipesJsonArray.getJSONObject(i);
                Recipe recipe = getRecipeObjectFromJsonObject(recipeJson);
                if (recipe != null) recipes.add(recipe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes.toArray(new Recipe[0]);
    }

    @Nullable
    private static Recipe getRecipeObjectFromJsonObject(JSONObject jsonObject) {
        try {
            int recipeId = jsonObject.getInt("id");
            String recipeName = jsonObject.getString("name");
            Ingredient[] ingredients = getIngredientObjectsFromJsonArray(jsonObject.getJSONArray("ingredients"), recipeId);
            Step[] steps = getStepObjectsFromJsonArray(jsonObject.getJSONArray("steps"), recipeId);
            int servings = jsonObject.getInt("servings");
            String imageUrlString = jsonObject.getString("image");
            return new Recipe(recipeId, recipeName, ingredients, steps, servings, imageUrlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Ingredient[] getIngredientObjectsFromJsonArray(JSONArray ingredientsJsonArray, int recipeId) {
        ArrayList<Ingredient> ingredients = new ArrayList<>(ingredientsJsonArray.length());
        try {
            for (int j = 0; j < ingredientsJsonArray.length(); j++) {
                JSONObject ingredientJson = ingredientsJsonArray.getJSONObject(j);
                Ingredient ingredient = getIngredientObjectFromJsonObject(ingredientJson, recipeId);
                if (ingredient != null) ingredients.add(ingredient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ingredients.toArray(new Ingredient[0]);
    }

    @Nullable
    private static Ingredient getIngredientObjectFromJsonObject(JSONObject ingredientJson, int recipeId) {
        try {
            int quantity = ingredientJson.getInt("quantity");
            String measureUnit = ingredientJson.getString("measure");
            String ingredientName = ingredientJson.getString("ingredient");
            return new Ingredient(recipeId, ingredientName, quantity, measureUnit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Step[] getStepObjectsFromJsonArray(JSONArray stepsJsonArray, int recipeId) {
        ArrayList<Step> steps = new ArrayList<>(stepsJsonArray.length());
        try {
            for (int j = 0; j < stepsJsonArray.length(); j++) {
                JSONObject stepJson = stepsJsonArray.getJSONObject(j);
                Step step = getStepObjectFromJsonObject(stepJson, recipeId);
                steps.add(step);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps.toArray(new Step[0]);
    }

    @Nullable
    private static Step getStepObjectFromJsonObject(JSONObject stepJson, int recipeId) {
        try {
            int stepOrder = stepJson.getInt("id");
            String shortDescription = stepJson.getString("shortDescription");
            String description = stepJson.getString("description");
            String videoUrlString = stepJson.getString("videoURL");
            String thumbnailUrlString = stepJson.getString("thumbnailURL");
            return new Step(recipeId, stepOrder, shortDescription, description,
                    videoUrlString, thumbnailUrlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
