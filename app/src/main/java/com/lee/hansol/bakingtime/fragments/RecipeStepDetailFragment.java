package com.lee.hansol.bakingtime.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.lee.hansol.bakingtime.models.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RecipeStepDetailFragment extends Fragment {
    private Unbinder unbinder;
    private OnPrevNextButtonClickListener prevNextButtonClickListener;
    private View rootView;

    @BindView(R.id.fragment_recipe_step_detail_short_description)
    TextView shortDescriptionView;
    @BindView(R.id.fragment_recipe_step_detail_exoplayerview)
    SimpleExoPlayerView exoPlayerView;
    @BindView(R.id.fragment_recipe_step_detail_description)
    TextView descriptionView;
    @BindView(R.id.fragment_recipe_step_detail_previous_btn)
    Button previousButton;
    @BindView(R.id.fragment_recipe_step_detail_next_btn)
    Button nextButton;

    public interface OnPrevNextButtonClickListener {
        void onPrevButtonClicked();
        void onNextButtonClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            prevNextButtonClickListener = (OnPrevNextButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPrevNextButtonClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initialize();
        return rootView;
    }

    private void initialize() {
        Step step = DataHelper.getInstance().getCurrentStepObject();
        if (step != null) {
            shortDescriptionView.setText(step.shortDescription);
            //TODO: add video
            descriptionView.setText(step.description);
        }
    }

    @OnClick({R.id.fragment_recipe_step_detail_previous_btn, R.id.fragment_recipe_step_detail_next_btn})
    void onButtonClick(View v) {
        if (v.getId() == R.id.fragment_recipe_step_detail_previous_btn)
            prevNextButtonClickListener.onPrevButtonClicked();
        else
            prevNextButtonClickListener.onNextButtonClicked();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void slideLeftRenewSlideRightEnter() {
        final Animator slideLeft = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_left_exit);
        slideLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initialize();
                slideLeft.removeAllListeners();
                slideRightEnter();
            }
        });
        slideLeft.setTarget(rootView);
        slideLeft.start();
    }

    private void slideRightEnter() {
        Animator slideRightEnter = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_right_enter);
        slideRightEnter.setTarget(rootView);
        slideRightEnter.start();
    }

    public void slideRightRenewSlideRightEnter() {
        final Animator slideRight = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_right_exit);
        slideRight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initialize();
                slideRight.removeAllListeners();
                slideRightEnter();
            }
        });
        slideRight.setTarget(rootView);
        slideRight.start();
    }

    public void slideLeftRenewSlideLeftEnter() {
        final Animator slideLeft = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_left_exit);
        slideLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initialize();
                slideLeft.removeAllListeners();
                slideLeftEnter();
            }
        });
        slideLeft.setTarget(rootView);
        slideLeft.start();
    }

    private void slideLeftEnter() {
        Animator slideLeftEnter = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_left_enter);
        slideLeftEnter.setTarget(rootView);
        slideLeftEnter.start();
    }
}
