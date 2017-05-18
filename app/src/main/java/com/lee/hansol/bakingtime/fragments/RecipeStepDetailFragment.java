package com.lee.hansol.bakingtime.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.lee.hansol.bakingtime.helpers.ExoPlayerHelper;
import com.lee.hansol.bakingtime.models.Step;
import com.lee.hansol.bakingtime.utils.StringUtils;
import com.lee.hansol.bakingtime.utils.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipeStepDetailFragment extends RenewableFragment implements View.OnTouchListener {
    private Unbinder unbinder;
    private Step step;
    private OnPrevNextButtonClickListener prevNextButtonClickListener;
    private ExoPlayerHelper playerHelper;
    public boolean isFullMode = false;
    private GestureDetectorCompat gestureDetector;
    private int swipeThreshold;

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
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        rootView.setOnTouchListener(this);
        initialize();
        return rootView;
    }

    private void initialize() {
        playerHelper = new ExoPlayerHelper(getActivity());
        gestureDetector = new GestureDetectorCompat(getActivity(), new SwipeListener());
        swipeThreshold = getResources().getInteger(R.integer.swipe_threshold);
        initializeViews();
    }

    private void initializeViews() {
        exitFullMode();
        playerHelper.clearExoPlayerResumePosition();
        step = DataStorage.getInstance().getCurrentStepObject();
        initializeViewContents();
    }

    public void exitFullMode() {
        if (!isFullMode) return;
        isFullMode = false;
        setExoPlayerViewToNormal();
        showAllViews();
        setWindowToNormal();
    }

    private void setExoPlayerViewToNormal() {
        showFullButton();
        setVideoSizeToNormal();
        setVideoBackgroundToNormal();
    }

    private void showFullButton() {
        exoFullButton.setVisibility(View.VISIBLE);
        exoFullExitButton.setVisibility(View.GONE);
    }

    private void setVideoSizeToNormal() {
        ViewGroup.LayoutParams params = exoPlayerViewContainer.getLayoutParams();
        params.width = 0;
        params.height = (int) getActivity().getResources().getDimension(R.dimen.video_height);
        exoPlayerViewContainer.setLayoutParams(params);
    }

    private void setVideoBackgroundToNormal() {
        TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[] {
                android.R.attr.colorBackground,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        exoPlayerViewContainer.setBackgroundColor(backgroundColor);
        array.recycle();
    }

    private void showAllViews() {
        shortDescriptionView.setVisibility(View.VISIBLE);
        descriptionView.setVisibility(View.VISIBLE);
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void setWindowToNormal() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void initializeViewContents() {
        if (step != null) {
            shortDescriptionView.setText(StringUtils.getStepShortDescText(getActivity(), step));
            descriptionView.setText(step.description);
            setupExoPlayerView();
            setupPrevNextButtonVisibility();
        }
    }

    private void setupExoPlayerView() {
        playerHelper.releaseExoPlayer();
        if ((step.videoUrlString == null) || step.videoUrlString.isEmpty())
            showVideoEmptyView();
        else if (!User.hasInternetConnection(getActivity()))
            showVideoUnplayableView();
        else
            showExoPlayerView();
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

    private void showExoPlayerView() {
        initializeExoPlayerView();
        brokenVideoImage.setVisibility(View.GONE);
        brokenVideoText.setVisibility(View.GONE);
        exoPlayerView.setVisibility(View.VISIBLE);
    }

    private void initializeExoPlayerView() {
        exitFullMode();
        SimpleExoPlayer exoPlayer = playerHelper.getInitializedExoPlayer(step);
        exoPlayerView.setPlayer(exoPlayer);
    }

    private void setupPrevNextButtonVisibility() {
        if (DataStorage.getInstance().hasNextStep()) nextButton.setVisibility(View.VISIBLE);
        else nextButton.setVisibility(View.INVISIBLE);
        if (DataStorage.getInstance().hasPreviousStep()) previousButton.setVisibility(View.VISIBLE);
        else previousButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        playerHelper.pauseExoPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        playerHelper.updateResumePositionAndReleaseExoPlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (needToReloadExoPlayer())
            initializeExoPlayerView();
    }

    private boolean needToReloadExoPlayer() {
        return (playerHelper.getExoPlayer() == null) && (exoPlayerView.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        playerHelper.clearExoPlayerResumePosition();
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
        enterFullMode();
    }

    private void enterFullMode() {
        if (isFullMode) return;
        isFullMode = true;
        setExoPlayerViewToFull();
        hideAllViewsExceptVideo();
        setWindowToFullScreen();
    }

    private void setExoPlayerViewToFull() {
        showFullExitButton();
        setVideoSizeMatchParent();
        setVideoBackgroundToBlack();
    }

    private void showFullExitButton() {
        exoFullButton.setVisibility(View.GONE);
        exoFullExitButton.setVisibility(View.VISIBLE);
    }

    private void setVideoSizeMatchParent() {
        ViewGroup.LayoutParams params = exoPlayerViewContainer.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        exoPlayerViewContainer.setLayoutParams(params);
    }

    private void setVideoBackgroundToBlack() {
        exoPlayerViewContainer.setBackgroundColor(Color.BLACK);
    }

    private void hideAllViewsExceptVideo() {
        shortDescriptionView.setVisibility(View.GONE);
        descriptionView.setVisibility(View.GONE);
        previousButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
    }

    private void setWindowToFullScreen() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @OnClick(R.id.exo_full_exit)
    void onFullExitButtonClick() {
        exitFullMode();
    }

    @Override
    public void renew() {
        initializeViews();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (turnsLandscapeWhilstVideoNotFullMode(newConfig))
            enterFullMode();
        else if (turnsPortraitWhilstVideoFullMode(newConfig))
            exitFullMode();
    }

    private boolean turnsLandscapeWhilstVideoNotFullMode(Configuration newConfig) {
        return (playerHelper.getExoPlayer() != null)
                && (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
                && !isFullMode;
    }

    private boolean turnsPortraitWhilstVideoFullMode(Configuration newConfig) {
        return (playerHelper.getExoPlayer() != null)
                && (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                && isFullMode;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return false;
    }

    private class SwipeListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isFullMode) return false;
            float fromX = e1.getX();
            float toX = e2.getX();
            if ((toX - fromX) <= -swipeThreshold) {
                swipeRight();
                return true;
            } else if ((toX - fromX) >= swipeThreshold) {
                swipeLeft();
                return true;
            }
            return false;
        }
    }

    private void swipeRight() {
        prevNextButtonClickListener.onNextButtonClicked();
    }

    private void swipeLeft() {
        prevNextButtonClickListener.onPrevButtonClicked();
    }
}
