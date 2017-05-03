package com.lee.hansol.bakingtime.loaders;

import android.content.Context;
import android.net.Network;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.JsonReader;
import android.util.Log;

import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;
import com.lee.hansol.bakingtime.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class RecipesLoaderFromInternet extends AsyncTaskLoader<Recipe[]> {
    private Recipe[] recipes;

    public RecipesLoaderFromInternet(Context context) { super(context); }

    @Override
    protected void onStartLoading() {
        if (recipes != null) deliverResult(recipes);
        else forceLoad();
    }

    @Override
    @NonNull
    public Recipe[] loadInBackground() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        JSONArray recipesJsonArray = null;
        Log.d("hello", "going!");
        try {
            String urlString = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/5907926b_baking/baking.json";
            recipesJsonArray = NetworkUtils.readJsonFromUrl(urlString);
//            URL url = new URL(urlString);
//            Log.d("hello", "url: " + url.toString());
//            String jsonString = "";
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            try {
//                Log.d("hello", "begin");
//                InputStream in = urlConnection.getInputStream();
//                Log.d("hello", "after");
//
//                Scanner scanner = new Scanner(in);
//                scanner.useDelimiter("\\A");
//
//                boolean hasInput = scanner.hasNext();
//                Log.d("hello", "jsonstring beginning: " + jsonString);
//                if (hasInput) {
//                    jsonString = scanner.next();
//                }
//                Log.d("hello", "jsonstring after: " + jsonString);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally{
//                urlConnection.disconnect();
//            }
//            JSONObject json = new JSONObject(jsonString);
//            Log.d("hello", "json object: " + json.toString());
//            recipesJsonArray = json.getJSONArray("");
            for (int i = 0; i < recipesJsonArray.length(); i++) {
                JSONObject recipeJson = recipesJsonArray.getJSONObject(i);
                int recipeId = recipeJson.getInt("id");
                String recipeName = recipeJson.getString("name");
                JSONArray ingredientsJsonArray = recipeJson.getJSONArray("ingredients");
                JSONArray stepsJsonArray = recipeJson.getJSONArray("steps");
                int servings = recipeJson.getInt("servings");
                String imageUrlString = recipeJson.getString("image");
                ArrayList<Ingredient> ingredients = new ArrayList<>(ingredientsJsonArray.length());
                for (int j = 0; j < ingredientsJsonArray.length(); j++) {
                    JSONObject ingredientJson = ingredientsJsonArray.getJSONObject(j);
                    int quantity = ingredientJson.getInt("quantity");
                    String measureUnit = ingredientJson.getString("measure");
                    String ingredientName = ingredientJson.getString("ingredient");
                    Ingredient ingredient = new Ingredient(recipeId, ingredientName, quantity, measureUnit);
                    ingredients.add(ingredient);
                }
                ArrayList<Step> steps = new ArrayList<>(stepsJsonArray.length());
                for (int j = 0; j < stepsJsonArray.length(); j++) {
                    JSONObject stepJson = stepsJsonArray.getJSONObject(j);
                    int stepOrder = stepJson.getInt("id");
                    String shortDescription = stepJson.getString("shortDescription");
                    String description = stepJson.getString("description");
                    String videoUrlString = stepJson.getString("videoURL");
                    String thumbnailUrlString = stepJson.getString("thumbnailURL");
                    Step step = new Step(recipeId, stepOrder, shortDescription, description,
                            videoUrlString, thumbnailUrlString);
                    steps.add(step);
                }
                Recipe recipe = new Recipe(recipeId, recipeName,
                        ingredients.toArray(new Ingredient[0]), steps.toArray(new Step[0]),
                        servings, imageUrlString);
                recipes.add(recipe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes.toArray(new Recipe[0]);
    }

    @Override
    public void deliverResult(@NonNull Recipe[] data) {
        recipes = data;
        super.deliverResult(data);
    }
}
