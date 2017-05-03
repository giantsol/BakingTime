package com.lee.hansol.bakingtime.models;

public class Recipe {
    public int recipeId;
    public String name;
    public Ingredient[] ingredients;
    public Step[] steps;
    public int servings;
    public String imageUrl;

    public Recipe(int recipeId, String name, Ingredient[] ingredients,
                  Step[] steps, int servings, String imageUrl) {
        this.recipeId = recipeId;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imageUrl = imageUrl;
    }
}
