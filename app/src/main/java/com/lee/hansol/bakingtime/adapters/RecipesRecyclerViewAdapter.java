package com.lee.hansol.bakingtime.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Recipe;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.id;
import static android.R.attr.translationZ;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.RecipeViewHolder> {
    private Context context;
    @NonNull private Recipe[] recipes = new Recipe[0];
    private final OnRecipeItemClickListener recipeItemClickListener;

    public interface OnRecipeItemClickListener {
        void onRecipeItemClick(Recipe recipe);
    }

    public RecipesRecyclerViewAdapter(OnRecipeItemClickListener recipeItemClickListener) {
        this.recipeItemClickListener = recipeItemClickListener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
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
            Animator animator = AnimatorInflater.loadAnimator(context, R.animator.raise);
            animator.setTarget(v);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    recipeItemClickListener.onRecipeItemClick(recipes[getAdapterPosition()]);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }
}
