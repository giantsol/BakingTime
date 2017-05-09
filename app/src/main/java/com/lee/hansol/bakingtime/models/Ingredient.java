package com.lee.hansol.bakingtime.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
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

    private Ingredient(Parcel in) {
        recipeId = in.readInt();
        name = in.readString();
        quantity = in.readInt();
        measureUnit = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recipeId);
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeString(measureUnit);
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel parcel) {
            return new Ingredient(parcel);
        }

        @Override
        public Ingredient[] newArray(int i) {
            return new Ingredient[i];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof  Ingredient)) return false;
        final Ingredient that = (Ingredient) obj;
        return (this.recipeId == that.recipeId) && (this.name.equals(that.name))
                && (this.quantity == that.quantity);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + recipeId;
        hash = 31 * hash + (name == null ? 0 : name.hashCode());
        hash = 31 * hash + quantity;
        return hash;
    }

}
