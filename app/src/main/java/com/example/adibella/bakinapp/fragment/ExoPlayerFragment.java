package com.example.adibella.bakinapp.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adibella.bakinapp.R;
import com.example.adibella.bakinapp.util.NetworkUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExoPlayerFragment extends Fragment implements Player.EventListener {
    private static final String MEDIA_URL_KEY = "media_url";
    private static final String THUMBNAIL_URL_KEY = "thumbnail_url";
    private static final String RESUME_POSITION_KEY = "resume_position";
    private static final String RESUME_WINDOW_KEY = "resume_window";
    private static final String SHOULD_AUTO_PLAY_KEY = "should_auto_play";

    private static final int SDK_VERSION = 23;

    @BindView(R.id.player_view)
    PlayerView playerView;

    private SimpleExoPlayer simpleExoPlayer;
    private String mediaUrl;
    private String thumbnailUrl;

    private long resumePosition;
    private int resumeWindow;
    private boolean shouldAutoPlay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldAutoPlay = true;
        resumePosition = C.TIME_UNSET;
        resumeWindow = C.INDEX_UNSET;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mediaUrl = savedInstanceState.getString(MEDIA_URL_KEY);
            thumbnailUrl = savedInstanceState.getString(THUMBNAIL_URL_KEY);
            resumePosition = savedInstanceState.getLong(RESUME_POSITION_KEY, C.TIME_UNSET);
            resumeWindow = savedInstanceState.getInt(RESUME_WINDOW_KEY, C.INDEX_UNSET);
            shouldAutoPlay = savedInstanceState.getBoolean(SHOULD_AUTO_PLAY_KEY, true);
        }
        View rootView = inflater.inflate(R.layout.player, container, false);

        ButterKnife.bind(this, rootView);
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Picasso.with(getContext())
                    .load(thumbnailUrl)
                    .into(target);
        } else {
            setPlayerDefaultArt();
        }

        playerInit(Uri.parse(mediaUrl));

        return rootView;
    }

    private void playerInit(Uri mediaUri) {
        if (simpleExoPlayer == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(factory);
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            playerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.addListener(this);
            MediaSource mediaSource = buildMediaSource(getContext(), mediaUri);
            simpleExoPlayer.prepare(mediaSource, true, false);
            simpleExoPlayer.setPlayWhenReady(shouldAutoPlay);
            simpleExoPlayer.seekTo(resumePosition);
        } else {
            MediaSource mediaSource = buildMediaSource(getContext(),mediaUri);
            simpleExoPlayer.prepare(mediaSource, true, false);
            simpleExoPlayer.setPlayWhenReady(shouldAutoPlay);
            simpleExoPlayer.seekTo(resumePosition);
        }
    }

    private MediaSource buildMediaSource(Context context, Uri uri) {
        String userAgent = Util.getUserAgent(getContext(), "BakinApp");
        MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                context, userAgent), new DefaultExtractorsFactory(), null, null);
        return mediaSource;
    }

    private void playerFree() {
        if (simpleExoPlayer != null) {
            resumePosition = simpleExoPlayer.getCurrentPosition();
            resumeWindow = simpleExoPlayer.getCurrentWindowIndex();
            shouldAutoPlay = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if (simpleExoPlayer != null) {
            resumePosition = simpleExoPlayer.getCurrentPosition();
            resumeWindow = simpleExoPlayer.getNextWindowIndex();
            shouldAutoPlay = simpleExoPlayer.getPlayWhenReady();
        }
        outState.putString(MEDIA_URL_KEY, mediaUrl);
        outState.putString(THUMBNAIL_URL_KEY, thumbnailUrl);
        outState.putLong(RESUME_POSITION_KEY, resumePosition);
        outState.putInt(RESUME_WINDOW_KEY, resumeWindow);
        outState.putBoolean(SHOULD_AUTO_PLAY_KEY, shouldAutoPlay);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= SDK_VERSION) {
            playerFree();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mediaUrl)) {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.seekTo(resumePosition);
            } else {
                playerInit(Uri.parse(mediaUrl));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > SDK_VERSION) {
            playerFree();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > SDK_VERSION) {
            if (!TextUtils.isEmpty(mediaUrl)) {
                playerInit(Uri.parse(mediaUrl));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerFree();
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            playerView.setDefaultArtwork(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            setPlayerDefaultArt();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void setPlayerDefaultArt() {
        playerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.default_image));
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
