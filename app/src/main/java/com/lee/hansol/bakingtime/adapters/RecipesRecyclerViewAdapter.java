package com.lee.hansol.bakingtime.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.lee.hansol.bakingtime.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.RecipeViewHolder> {
    private Context context;

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

    private class RecipeItemClickAnimatorListener extends AnimatorListenerAdapter {
        int clickedItemIndex = 0;
        @Override
        public void onAnimationEnd(Animator animation) {
            recipeItemClickListener.onRecipeItemClick(clickedItemIndex);
        }

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = DataStorage.getInstance().getRecipeObjectAt(position);
        if (recipe != null) {
            holder.name.setText(recipe.name);
            holder.servings.setText(String.format(Locale.getDefault(),
                    context.getString(R.string.text_servings_placeholder),
                    recipe.servings));
            setImageView(holder.image, recipe.imageUrlString);
        }
    }

    private void setImageView(ImageView imageView, String imageUrlString) {
        if (imageUrlString.equals(""))
            Picasso.with(context).load(R.drawable.no_image).into(imageView);
        else
            Picasso.with(context).load(imageUrlString).error(R.drawable.no_image).into(imageView);
    }

    @Override
    public int getItemCount() {
        return DataStorage.getInstance().getAllRecipes().length;
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
            recipeItemClickAnimatorListener.clickedItemIndex = getAdapterPosition();
            recipeItemClickAnimator.start();
        }
    }

}
