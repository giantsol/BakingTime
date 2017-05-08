package com.lee.hansol.bakingtime.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private StepsRecyclerViewAdapter.OnStepItemClickListener stepItemClickListener;

    private final String BUNDLE_KEY_SAVED_RECIPE_OBJECT = "saved_recipe_object";

    public static RecipeStepListFragment getInstance(Recipe recipe) {
        RecipeStepListFragment fragment = new RecipeStepListFragment();
        fragment.recipe = recipe;
        return fragment;
    }

    @BindView(R.id.fragment_recipe_step_list_ingredients_view) RecyclerView ingredientsRecyclerView;
    @BindView(R.id.fragment_recipe_step_list_steps_view) RecyclerView stepsRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            stepItemClickListener = (StepsRecyclerViewAdapter.OnStepItemClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnStepItemClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        restoreSavedStateIfExists(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        initialize();
        return view;
    }

    private void restoreSavedStateIfExists(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(BUNDLE_KEY_SAVED_RECIPE_OBJECT);
        }
    }

    private void initialize() {
        initializeIngredientsRecyclerView();
        initializeStepsRecyclerView();
    }

    private void initializeIngredientsRecyclerView() {
        ingredientsRecyclerView.setHasFixedSize(true);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        ingredientsViewAdapter = new IngredientsRecyclerViewAdapter(recipe.ingredients);
        ingredientsRecyclerView.setAdapter(ingredientsViewAdapter);
    }

    private void initializeStepsRecyclerView() {
        stepsRecyclerView.setHasFixedSize(true);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsViewAdapter = new StepsRecyclerViewAdapter(stepItemClickListener, recipe.steps);
        stepsRecyclerView.setAdapter(stepsViewAdapter);
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
