package com.lee.hansol.bakingtime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepListFragment extends Fragment {
    private Unbinder unbinder;
    private Recipe recipe;

    public static RecipeStepListFragment getInstance(Recipe recipe) {
        RecipeStepListFragment fragment = new RecipeStepListFragment();
        fragment.recipe = recipe;
        return fragment;
    }

    @BindView(R.id.ingredients_recyclerview) RecyclerView ingredientsView;
    @BindView(R.id.temp) TextView temp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        initializeIngredientsRecyclerView();
        temp.setText(recipe.name);
        return view;
    }

    private void initializeIngredientsRecyclerView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
