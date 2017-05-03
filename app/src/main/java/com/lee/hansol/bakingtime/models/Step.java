package com.lee.hansol.bakingtime.models;

public class Step {
    public int recipeId;
    public int stepOrder;
    public String shortDescription;
    public String description;
    public String videoUrl;
    public String thumbnailUrl;

    public Step(int recipeId, int stepOrder, String shortDescription,
                String description, String videoUrl, String thumbnailUrl) {
        this.recipeId = recipeId;
        this.stepOrder = stepOrder;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }
}
