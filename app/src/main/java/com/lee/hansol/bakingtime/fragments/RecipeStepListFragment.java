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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

    @BindView(R.id.fragment_recipe_step_list_ingredients_view) RecyclerView ingredientsRecyclerView;
    @BindView(R.id.fragment_recipe_step_list_steps_view) RecyclerView stepsRecyclerView;
    @BindView(R.id.fragment_recipe_step_list_slider) SlidingUpPanelLayout slider;
    @BindView(R.id.fragment_recipe_step_list_click_prevent_screen) View transparentScreen;

    public static RecipeStepListFragment getInstance(Recipe recipe) {
        RecipeStepListFragment fragment = new RecipeStepListFragment();
        fragment.recipe = recipe;
        return fragment;
    }

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
        slider.addPanelSlideListener(sliderListener);
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

    private SlidingUpPanelLayout.PanelSlideListener sliderListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {

        }

        @Override
        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                        SlidingUpPanelLayout.PanelState newState) {
            boolean isSliderExpanded = newState == SlidingUpPanelLayout.PanelState.EXPANDED;
            if (isSliderExpanded) {
                disableStepListClick();
            } else {
                enableStepListClick();
            }
        }
    };

    private void disableStepListClick() {
        transparentScreen.setVisibility(View.VISIBLE);
    }

    private void enableStepListClick() {
        transparentScreen.setVisibility(View.GONE);
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
