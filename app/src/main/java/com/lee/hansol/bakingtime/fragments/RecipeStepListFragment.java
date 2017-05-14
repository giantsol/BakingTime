package com.lee.hansol.bakingtime.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.adapters.IngredientsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepListFragment extends Fragment {
    private Unbinder unbinder;
    private StepsRecyclerViewAdapter.OnStepItemClickListener stepItemClickListener;
    private View rootView;
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
        rootView = inflater.inflate(R.layout.fragment_recipe_step_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initialize(savedInstanceState);
        return rootView;
    }

    private void initialize(Bundle savedInstanceState) {
        initializeIngredientsRecyclerView();
        initializeStepsRecyclerView();
        slider.addPanelSlideListener(sliderListener);
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

    @Override
    public void onResume() {
        super.onResume();
        determineStepListItemClickable();
        notifyAdapter();
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

    public void closeSlider() {
        slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void fadeOutRenewFadeIn() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Animator fadeOut = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_fade_out);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initialize(null);
                animation.removeAllListeners();
                fadeIn();
            }
        });
        fadeOut.setTarget(rootView);
        fadeOut.start();
    }

    private void fadeIn() {
        Animator fadeIn = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_fade_in);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                animation.removeAllListeners();
            }
        });
        fadeIn.setTarget(rootView);
        fadeIn.start();
    }

    public void notifyAdapter() {
        stepsRecyclerView.getAdapter().notifyDataSetChanged();
        if (DataHelper.getInstance().getCurrentStepIndex() != -1)
            stepsRecyclerView.scrollToPosition(DataHelper.getInstance().getCurrentStepIndex());
    }
}
