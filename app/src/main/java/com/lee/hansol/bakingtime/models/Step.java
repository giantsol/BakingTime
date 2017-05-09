package com.lee.hansol.bakingtime.models;

import android.os.Parcel;
import android.os.Parcelable;

import static android.R.attr.name;

public class Step implements Parcelable {
    public int recipeId;
    public int stepOrder;
    public String shortDescription;
    public String description;
    public String videoUrlString;
    public String thumbnailUrlString;

    public Step(int recipeId, int stepOrder, String shortDescription,
                String description, String videoUrlString, String thumbnailUrlString) {
        this.recipeId = recipeId;
        this.stepOrder = stepOrder;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrlString = videoUrlString;
        this.thumbnailUrlString = thumbnailUrlString;
    }

    private Step(Parcel in) {
        recipeId = in.readInt();
        stepOrder = in.readInt();
        shortDescription = in.readString();
        description = in.readString();
        videoUrlString = in.readString();
        thumbnailUrlString = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recipeId);
        dest.writeInt(stepOrder);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoUrlString);
        dest.writeString(thumbnailUrlString);
    }

    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel parcel) {
            return new Step(parcel);
        }

        @Override
        public Step[] newArray(int i) {
            return new Step[i];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || !(obj instanceof Step)) return false;
        final Step that = (Step) obj;
        return (this.recipeId == that.recipeId) && (this.stepOrder == that.stepOrder)
                && (this.shortDescription.equals(that.shortDescription));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + recipeId;
        hash = 31 * hash + stepOrder;
        hash = 31 * hash + (shortDescription == null ? 0 : shortDescription.hashCode());
        return hash;
    }

}
