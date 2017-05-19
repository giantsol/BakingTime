package com.lee.hansol.bakingtime.helpers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Step;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class ExoPlayerHelper implements ExoPlayer.EventListener {
    private final Context context;
    @Nullable private SimpleExoPlayer exoPlayer;
    public int exoResumeWindow;
    public long exoResumePosition;

    public ExoPlayerHelper(Context context) { this.context = context; }

    public void clearExoPlayerResumePosition() {
        exoResumeWindow = C.INDEX_UNSET;
        exoResumePosition = C.TIME_UNSET;
    }

    public void setExoPlayerResumePosition(int resumeWindow, long resumePosition) {
        exoResumeWindow = resumeWindow;
        exoResumePosition = resumePosition;
    }

    public void releaseExoPlayer() {
        if (exoPlayer != null) {
            pauseExoPlayer();
            exoPlayer.stop();
            exoPlayer.release();
            log(context.getString(R.string.log_release_exoplayer));
        }
        exoPlayer = null;
    }

    public void pauseExoPlayer() {
        if ((exoPlayer != null) && (exoPlayer.getPlaybackState() == ExoPlayer.STATE_READY))
            exoPlayer.setPlayWhenReady(false);
    }

    @Nullable
    public SimpleExoPlayer getExoPlayer() { return exoPlayer; }

    public SimpleExoPlayer getInitializedExoPlayer(Step step) {
        if (exoPlayer != null) releaseExoPlayer();
        TrackSelector trackSelector = new DefaultTrackSelector();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        Uri videoUri = Uri.parse(step.videoUrlString);
        MediaSource mediaSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                context, userAgent), new DefaultExtractorsFactory(), null, null);

        boolean hasResumePosition = exoResumeWindow != C.INDEX_UNSET;
        if (hasResumePosition) {
            exoPlayer.seekTo(exoResumeWindow, exoResumePosition);
        }
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(this);
        return exoPlayer;
    }

    public void updateResumePositionAndReleaseExoPlayer() {
        if (exoPlayer != null) {
            updateExoPlayerResumePosition();
            releaseExoPlayer();
        }
    }

    public void updateExoPlayerResumePosition() {
        exoResumeWindow = exoPlayer.getCurrentWindowIndex();
        exoResumePosition = exoPlayer.isCurrentWindowSeekable() ? Math.max(0, exoPlayer.getCurrentPosition())
                : C.TIME_UNSET;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            if (exoPlayer != null)
                exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
