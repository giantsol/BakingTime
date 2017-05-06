package com.lee.hansol.bakingtime.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Arrays;

public class Recipe implements Parcelable {
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

    private Recipe(Parcel in) {
        this.recipeId = in.readInt();
        this.name = in.readString();
        Object[] temp;
        temp = in.readArray(Ingredient.class.getClassLoader());
        this.ingredients = Arrays.copyOf(temp, temp.length, Ingredient[].class);
        temp = in.readArray(Ingredient.class.getClassLoader());
        this.steps = Arrays.copyOf(temp, temp.length, Step[].class);
        this.servings = in.readInt();
        this.imageUrlString = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recipeId);
        dest.writeString(name);
        dest.writeArray(ingredients);
        dest.writeArray(steps);
//        dest.writeParcelableArray(ingredients, flags);
//        dest.writeParcelableArray(steps, flags);
        dest.writeInt(servings);
        dest.writeString(imageUrlString);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            return new Recipe(parcel);
        }

        @Override
        public Recipe[] newArray(int i) {
            return new Recipe[i];
        }

    };
}
