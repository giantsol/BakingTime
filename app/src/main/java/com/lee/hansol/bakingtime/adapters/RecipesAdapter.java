package com.lee.hansol.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lee.hansol.bakingtime.models.Recipe;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private Context context;
    @NonNull private Recipe[] recipes = new Recipe[0];

    public RecipesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {

    }

    public void setRecipes(@NonNull Recipe[] recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        RecipeViewHolder(View view) {
            super(view);
        }
    }
}
