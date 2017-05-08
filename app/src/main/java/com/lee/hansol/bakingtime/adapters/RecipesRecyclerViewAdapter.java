package com.lee.hansol.bakingtime.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.RecipeViewHolder> {
    private Context context;
    @NonNull public Recipe[] recipes = new Recipe[0];

    private final OnRecipeItemClickListener recipeItemClickListener;
    private final Animator recipeItemClickAnimator;
    private final RecipeItemClickAnimatorListener recipeItemClickAnimatorListener;

    public interface OnRecipeItemClickListener {
        void onRecipeItemClick(int recipeIndex);
    }

    public RecipesRecyclerViewAdapter(Context context, OnRecipeItemClickListener recipeItemClickListener) {
        this.context = context;
        this.recipeItemClickListener = recipeItemClickListener;
        this.recipeItemClickAnimator = AnimatorInflater.loadAnimator(context, R.animator.raise);
        this.recipeItemClickAnimatorListener = new RecipeItemClickAnimatorListener();
        this.recipeItemClickAnimator.addListener(recipeItemClickAnimatorListener);
    }

    private class RecipeItemClickAnimatorListener implements Animator.AnimatorListener {
        int whichRecipe = 0;

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            recipeItemClickListener.onRecipeItemClick(whichRecipe);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(holderView);
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

    @Override
    public int getItemCount() {
        return recipes.length;
    }

    public void setRecipesAndRefresh(@NonNull Recipe[] recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_list_item_name) TextView name;
        @BindView(R.id.recipe_list_item_servings) TextView servings;
        @BindView(R.id.recipe_list_item_image) ImageView image;
        @BindView(R.id.recipe_list_item_parent) CardView parent;

        RecipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recipeItemClickAnimator.setTarget(v);
            recipeItemClickAnimatorListener.whichRecipe = getAdapterPosition();
            recipeItemClickAnimator.start();
        }
    }

}
