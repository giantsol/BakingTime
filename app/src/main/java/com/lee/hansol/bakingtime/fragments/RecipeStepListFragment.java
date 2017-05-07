package com.lee.hansol.bakingtime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.adapters.IngredientsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.models.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepListFragment extends Fragment {
    private Unbinder unbinder;
    private Recipe recipe;
    private IngredientsRecyclerViewAdapter ingredientsViewAdapter;
    private StepsRecyclerViewAdapter stepsViewAdapter;

    private final String BUNDLE_KEY_SAVED_RECIPE_OBJECT = "saved_recipe_object";

    public static RecipeStepListFragment getInstance(Recipe recipe) {
        RecipeStepListFragment fragment = new RecipeStepListFragment();
        fragment.recipe = recipe;
        return fragment;
    }

    @BindView(R.id.ingredients_recyclerview) RecyclerView ingredientsView;
    @BindView(R.id.fragment_recipe_step_list_recyclerview) RecyclerView stepsView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        restoreSavedStateIfExists(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        final View ingredients = view.findViewById(R.id.ingredients);
        ViewTreeObserver observer = ingredients.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ingredients.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setPaddingToStepsView(ingredients.getHeight());
                }
            });
        }

        initializeIngredientsRecyclerView();
        initializeStepsRecyclerView();
        return view;
    }

    private void setPaddingToStepsView(int paddingTop) {
        stepsView.setPadding(0, paddingTop, 0, 0);
        stepsView.getLayoutManager().scrollToPosition(0);
    }

    private void restoreSavedStateIfExists(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(BUNDLE_KEY_SAVED_RECIPE_OBJECT);
        }
    }

    private void initializeIngredientsRecyclerView() {
        ingredientsView.setHasFixedSize(true);
        ingredientsView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        ingredientsViewAdapter = new IngredientsRecyclerViewAdapter(recipe.ingredients);
        ingredientsView.setAdapter(ingredientsViewAdapter);
    }

    private void initializeStepsRecyclerView() {
        stepsView.setHasFixedSize(true);
        stepsView.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsViewAdapter = new StepsRecyclerViewAdapter(recipe.steps);
        stepsView.setAdapter(stepsViewAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_KEY_SAVED_RECIPE_OBJECT, recipe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
