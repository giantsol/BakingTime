package com.lee.hansol.bakingtime.models;

public class Ingredient {
    public int recipeId;
    public String name;
    public int quantity;
    public String measureUnit;

    public Ingredient(int recipeId, String name, int quantity, String measureUnit) {
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.measureUnit = measureUnit;
    }
}
