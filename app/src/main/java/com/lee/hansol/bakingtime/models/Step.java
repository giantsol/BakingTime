package com.lee.hansol.bakingtime.models;

public class Step {
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
}
