package com.lee.hansol.bakingtime.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.lee.hansol.bakingtime.models.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.lee.hansol.bakingtime.utils.LogUtils.context;
import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipeStepDetailFragment extends Fragment {
    private Unbinder unbinder;
    private OnPrevNextButtonClickListener prevNextButtonClickListener;
    private View rootView;
    private SimpleExoPlayer exoPlayer;

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
            descriptionView.setText(step.description);

            if (exoPlayer != null) exoPlayer.release();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(true);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                    Util.getUserAgent(getActivity(), "BakingTime"));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource videoSource = new ExtractorMediaSource(Uri.parse(step.videoUrlString),
                    dataSourceFactory, extractorsFactory, null, null);
            exoPlayer.prepare(videoSource);
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
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Animator slideLeft = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_left_exit);
        slideLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initialize();
                animation.removeAllListeners();
                slideRightEnter();
            }
        });
        slideLeft.setTarget(rootView);
        slideLeft.start();
    }

    private void slideRightEnter() {
        Animator slideRightEnter = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_right_enter);
        slideRightEnter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                animation.removeAllListeners();
            }
        });
        slideRightEnter.setTarget(rootView);
        slideRightEnter.start();
    }

    public void slideRightRenewSlideRightEnter() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Animator slideRight = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_right_exit);
        slideRight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initialize();
                animation.removeAllListeners();
                slideRightEnter();
            }
        });
        slideRight.setTarget(rootView);
        slideRight.start();
    }

    public void slideLeftRenewSlideLeftEnter() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Animator slideLeft = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_left_exit);
        slideLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                initialize();
                animation.removeAllListeners();
                slideLeftEnter();
            }
        });
        slideLeft.setTarget(rootView);
        slideLeft.start();
    }

    private void slideLeftEnter() {
        Animator slideLeftEnter = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_left_enter);
        slideLeftEnter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                animation.removeAllListeners();
            }
        });
        slideLeftEnter.setTarget(rootView);
        slideLeftEnter.start();
    }

}
