package com.lee.hansol.bakingtime.helpers;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.lee.hansol.bakingtime.models.Recipe;

public class ActionBarHelper {
    @Nullable private final ActionBar actionBar;

    public ActionBarHelper(@Nullable ActionBar actionBar) { this.actionBar = actionBar; }

    public void setHomeButtonEnabled() {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void setTitleToRecipeName(@Nullable Recipe recipe) {
        if ((actionBar != null) && (recipe != null))
            actionBar.setTitle(recipe.name);
    }

    public void setTitle(@Nullable String title) {
        if ((actionBar != null) && (title != null))
            actionBar.setTitle(title);

    }
}
