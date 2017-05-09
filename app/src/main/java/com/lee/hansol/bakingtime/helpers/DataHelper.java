package com.lee.hansol.bakingtime.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

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

    //Recipes

    public void setRecipes(@NonNull Recipe[] recipes) { this.recipes = recipes; }

    public void setCurrentRecipeIndex(int index) {
        if (index >= recipes.length)
            throw new RuntimeException("invalid index");
        currentRecipeIndex = index;
        currentRecipeObject = recipes[currentRecipeIndex];
        ingredients = currentRecipeObject.ingredients;
        steps = currentRecipeObject.steps;
    }

    @Nullable
    public Recipe getRecipeObjectAt(int index) {
        if (index >= recipes.length) return null;
        else return recipes[index];
    }

    public Recipe[] getAllRecipes() { return recipes; }

    @Nullable
    public Recipe getCurrentRecipeObject() { return currentRecipeObject; }

    public int getCurrentRecipeIndex() { return currentRecipeIndex; }

    //Steps

    public void setCurrentStepIndex(int index) {
        if (index >= steps.length)
            throw new RuntimeException("invalid index");
        currentStepIndex = index;
        currentStepObject = steps[currentStepIndex];
    }

    @Nullable
    public Step getStepObjectAt(int index) {
        if (index >= steps.length) return null;
        else return steps[index];
    }

    public Step[] getAllSteps() { return steps; }

    @Nullable
    public Step getCurrentStepObject() { return currentStepObject; }

    public int getCurrentStepIndex() { return currentStepIndex; }


    //Ingredients

    @Nullable
    public Ingredient getIngredientObjectAt(int index) {
        if (index >= ingredients.length) return null;
        else return ingredients[index];
    }

    public Ingredient[] getAllIngredients() { return ingredients; }

}
