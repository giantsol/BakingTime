package com.lee.hansol.bakingtime.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.adapters.IngredientsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepListFragment extends Fragment {
    private Unbinder unbinder;
    private StepsRecyclerViewAdapter.OnStepItemClickListener stepItemClickListener;
    public boolean isSliderOpen = false;

    @BindView(R.id.fragment_recipe_step_list_ingredients_view) RecyclerView ingredientsRecyclerView;
    @BindView(R.id.fragment_recipe_step_list_steps_view) RecyclerView stepsRecyclerView;
    @BindView(R.id.fragment_recipe_step_list_slider) SlidingUpPanelLayout slider;
    @BindView(R.id.fragment_recipe_step_list_click_prevent_screen) View transparentScreen;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            stepItemClickListener = (StepsRecyclerViewAdapter.OnStepItemClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnStepItemClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initialize();
        return view;
    }

    private void initialize() {
        initializeIngredientsRecyclerView();
        initializeStepsRecyclerView();
        slider.addPanelSlideListener(sliderListener);
    }

    private void initializeIngredientsRecyclerView() {
        ingredientsRecyclerView.setHasFixedSize(true);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        ingredientsRecyclerView.setAdapter(new IngredientsRecyclerViewAdapter());
    }

    private void initializeStepsRecyclerView() {
        stepsRecyclerView.setHasFixedSize(true);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stepsRecyclerView.setAdapter(new StepsRecyclerViewAdapter(stepItemClickListener));
    }

    private SlidingUpPanelLayout.PanelSlideListener sliderListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {

        }

        @Override
        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                        SlidingUpPanelLayout.PanelState newState) {
            isSliderOpen = newState == SlidingUpPanelLayout.PanelState.EXPANDED;
            if (isSliderOpen) {
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

    public void closeSlider() {
        slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
