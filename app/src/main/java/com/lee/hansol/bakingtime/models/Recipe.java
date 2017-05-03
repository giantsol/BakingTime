package com.lee.hansol.bakingtime.models;

import android.support.annotation.NonNull;

public class Recipe {
    public int recipeId;
    public String name;
    @NonNull public Ingredient[] ingredients;
    @NonNull public Step[] steps;
    public int servings;
    public String imageUrlString;

    public Recipe(int recipeId, String name, @NonNull Ingredient[] ingredients,
                  @NonNull Step[] steps, int servings, String imageUrlString) {
        this.recipeId = recipeId;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imageUrlString = imageUrlString;
    }
}
