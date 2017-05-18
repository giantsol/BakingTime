package com.lee.hansol.bakingtime.fragments;

import android.app.Activity;
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
import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipeStepListFragment extends RenewableFragment {
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
            stepItemClickListener = (StepsRecyclerViewAdapter.OnStepItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnStepItemClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initialize();
        return rootView;
    }

    private void initialize() {
        slider.addPanelSlideListener(sliderListener);
        initializeViews();
    }

    private SlidingUpPanelLayout.PanelSlideListener sliderListener = new SlidingUpPanelLayout.PanelSlideListener() {
        @Override
        public void onPanelSlide(View panel, float slideOffset) {

        }

        @Override
        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                        SlidingUpPanelLayout.PanelState newState) {
            isSliderOpen = newState == SlidingUpPanelLayout.PanelState.EXPANDED;
            determineStepListItemClickable();
        }
    };

    private void initializeViews() {
        initializeIngredientsRecyclerView();
        initializeStepsRecyclerView();
    }

    private void initializeIngredientsRecyclerView() {
        ingredientsRecyclerView.setHasFixedSize(true);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientsRecyclerView.setAdapter(new IngredientsRecyclerViewAdapter());
    }

    private void initializeStepsRecyclerView() {
        stepsRecyclerView.setHasFixedSize(true);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stepsRecyclerView.setAdapter(new StepsRecyclerViewAdapter(stepItemClickListener));
    }

    @Override
    public void onResume() {
        super.onResume();
        determineStepListItemClickable();
        scrollToWhereItShouldBe();
    }

    private void determineStepListItemClickable() {
        if (isSliderOpen) {
            disableStepListClick();
        } else {
            enableStepListClick();
        }
    }

    private void disableStepListClick() {
        transparentScreen.setVisibility(View.VISIBLE);
    }

    private void enableStepListClick() {
        transparentScreen.setVisibility(View.GONE);
    }

    private void scrollToWhereItShouldBe() {
        if (DataStorage.getInstance().getCurrentStepIndex() != -1)
            stepsRecyclerView.scrollToPosition(DataStorage.getInstance().getCurrentStepIndex());
        else
            stepsRecyclerView.scrollToPosition(0);
    }

    public void closeSlider() {
        slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void renew() {
        ingredientsRecyclerView.getAdapter().notifyDataSetChanged();
        stepsRecyclerView.getAdapter().notifyDataSetChanged();
        scrollToWhereItShouldBe();
    }
}
