package com.lee.hansol.bakingtime.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.lee.hansol.bakingtime.models.Step;
import com.lee.hansol.bakingtime.utils.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RecipeStepDetailFragment extends Fragment {
    private Unbinder unbinder;
    private Step step;
    private OnPrevNextButtonClickListener prevNextButtonClickListener;
    private View rootView;
    @Nullable private SimpleExoPlayer exoPlayer;
    public boolean isFullMode = false;
    private int resumeWindow;
    private long resumePosition;

    @BindView(R.id.fragment_recipe_step_detail_short_description) TextView shortDescriptionView;
    @BindView(R.id.fragment_recipe_step_detail_exoplayerview) SimpleExoPlayerView exoPlayerView;
    @BindView(R.id.fragment_recipe_step_detail_description) TextView descriptionView;
    @BindView(R.id.fragment_recipe_step_detail_previous_btn) Button previousButton;
    @BindView(R.id.fragment_recipe_step_detail_next_btn) Button nextButton;
    @BindView(R.id.fragment_recipe_step_detail_broken_video_image) ImageView brokenVideoImage;
    @BindView(R.id.fragment_recipe_step_detail_broken_video_text) TextView brokenVideoText;
    @BindView(R.id.exo_full) ImageButton exoFullButton;
    @BindView(R.id.exo_full_exit) ImageButton exoFullExitButton;
    @BindView(R.id.fragment_recipe_step_detail_exoplayerview_container) View exoPlayerViewContainer;

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
        clearExoPlayerResumePosition();
        step = DataHelper.getInstance().getCurrentStepObject();
        if (step != null) {
            shortDescriptionView.setText(step.shortDescription);
            descriptionView.setText(step.description);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setExoPlayerView();
    }

    private void setExoPlayerView() {
        if (exoPlayer != null) releaseExoPlayer();
        if ((step.videoUrlString == null) || step.videoUrlString.isEmpty()) {
            showVideoEmptyView();
        } else if (!User.hasInternetConnection(getActivity())) {
            showVideoUnplayableView();
        } else {
            initializeExoPlayerView();
            showExoPlayerView();
        }
    }

    private void releaseExoPlayer() {
        if (exoPlayer != null) {
            pauseExoPlayer();
            exoPlayer.stop();
            exoPlayer.release();
        }
        exoPlayer = null;
    }

    private void showVideoEmptyView() {
        brokenVideoImage.setVisibility(View.VISIBLE);
        brokenVideoText.setVisibility(View.VISIBLE);
        brokenVideoText.setText(getString(R.string.text_broken_video));
        exoPlayerView.setVisibility(View.GONE);
    }

    private void showVideoUnplayableView() {
        brokenVideoImage.setVisibility(View.VISIBLE);
        brokenVideoText.setVisibility(View.VISIBLE);
        brokenVideoText.setText(getString(R.string.text_no_internet));
        exoPlayerView.setVisibility(View.GONE);
    }

    private void initializeExoPlayerView() {
        initializeExoPlayer();
        exoPlayerView.setPlayer(exoPlayer);
    }

    private void initializeExoPlayer() {
        TrackSelector trackSelector = new DefaultTrackSelector();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
        String userAgent = Util.getUserAgent(getActivity(), getString(R.string.app_name));
        Uri videoUri = Uri.parse(step.videoUrlString);
        MediaSource mediaSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);

        boolean hasResumePosition = resumeWindow != C.INDEX_UNSET;
        if (hasResumePosition) {
            exoPlayer.seekTo(resumeWindow, resumePosition);
        }
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        exitVideoFullMode();
    }

    private void showExoPlayerView() {
        brokenVideoImage.setVisibility(View.GONE);
        brokenVideoText.setVisibility(View.GONE);
        exoPlayerView.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.fragment_recipe_step_detail_previous_btn, R.id.fragment_recipe_step_detail_next_btn})
    void onPrevNextButtonClick(View v) {
        if (v.getId() == R.id.fragment_recipe_step_detail_previous_btn)
            prevNextButtonClickListener.onPrevButtonClicked();
        else
            prevNextButtonClickListener.onNextButtonClicked();
    }

    @OnClick(R.id.exo_full)
    void onFullButtonClick() {
        enterVideoFullMode();
    }

    private void enterVideoFullMode() {
        if (isFullMode) return;
        isFullMode = true;
        toggleFullButton();
        hideAllViewsExceptVideo();
        setVideoSizeMatchParent();
        setWindowToFullScreen();
        setVideoBackgroundToBlack();
    }

    private void toggleFullButton() {
        if (isFullMode) {
            exoFullButton.setVisibility(View.GONE);
            exoFullExitButton.setVisibility(View.VISIBLE);
        } else {
            exoFullButton.setVisibility(View.VISIBLE);
            exoFullExitButton.setVisibility(View.GONE);
        }
    }

    private void hideAllViewsExceptVideo() {
        shortDescriptionView.setVisibility(View.GONE);
        descriptionView.setVisibility(View.GONE);
        previousButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
    }

    private void setVideoSizeMatchParent() {
        ViewGroup.LayoutParams params = exoPlayerViewContainer.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        exoPlayerViewContainer.setLayoutParams(params);
    }

    private void setWindowToFullScreen() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    private void setVideoBackgroundToBlack() {
        exoPlayerViewContainer.setBackgroundColor(Color.BLACK);
    }

    @OnClick(R.id.exo_full_exit)
    void onFullExitButtonClick() {
        exitVideoFullMode();
    }

    public void exitVideoFullMode() {
        if (!isFullMode) return;
        isFullMode = false;
        toggleFullButton();
        showAllViews();
        setVideoSizeToNormal();
        setWindowToNormal();
        setVideoBackgroundToNormal();
    }

    private void showAllViews() {
        shortDescriptionView.setVisibility(View.VISIBLE);
        descriptionView.setVisibility(View.VISIBLE);
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void setVideoSizeToNormal() {
        ViewGroup.LayoutParams params = exoPlayerViewContainer.getLayoutParams();
        params.width = 0;
        params.height = (int) getActivity().getResources().getDimension(R.dimen.video_height);
        exoPlayerViewContainer.setLayoutParams(params);
    }

    private void setWindowToNormal() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void setVideoBackgroundToNormal() {
        TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[] {
                android.R.attr.colorBackground,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        exoPlayerViewContainer.setBackgroundColor(backgroundColor);
        array.recycle();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseExoPlayer();
    }

    private void pauseExoPlayer() {
        if ((exoPlayer != null) && (exoPlayer.getPlaybackState() == ExoPlayer.STATE_READY))
            exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            updateExoPlayerResumePosition();
            releaseExoPlayer();
        }
    }

    private void updateExoPlayerResumePosition() {
        resumeWindow = exoPlayer.getCurrentWindowIndex();
        resumePosition = exoPlayer.isCurrentWindowSeekable() ? Math.max(0, exoPlayer.getCurrentPosition())
                : C.TIME_UNSET;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        clearExoPlayerResumePosition();
    }

    private void clearExoPlayerResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    public void slideLeftRenewSlideRightEnter() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Animator slideLeft = AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_slide_left_exit);
        slideLeft.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                renew();
                animation.removeAllListeners();
                slideRightEnter();
            }
        });
        slideLeft.setTarget(rootView);
        slideLeft.start();
    }

    private void renew() {
        initialize();
        setExoPlayerView();
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
                renew();
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
                renew();
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
