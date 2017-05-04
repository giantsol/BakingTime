package com.lee.hansol.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Recipe;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private Context context;
    @NonNull private Recipe[] recipes = new Recipe[0];

    public RecipesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(holderView);
    }

    @Override
    public int getItemCount() {
        return recipes.length;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipes[position];
        holder.name.setText(recipe.name);
        holder.servings.setText(String.format(Locale.getDefault(),
                context.getString(R.string.text_servings_placeholder),
                recipe.servings));
        holder.image.setBackgroundResource(R.drawable.ic_assignment_ind_black_24dp);
    }

    public void setRecipesAndRefresh(@NonNull Recipe[] recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_list_item_name) TextView name;
        @BindView(R.id.recipe_list_item_servings) TextView servings;
        @BindView(R.id.recipe_list_item_image) ImageView image;

        RecipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
