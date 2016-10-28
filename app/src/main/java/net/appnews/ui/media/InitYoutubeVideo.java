package net.appnews.ui.media;

import android.view.View;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import net.appnews.R;

/**
 * Created by DongNguyen on 10/19/16.
 */

public class InitYoutubeVideo {

    private final int videoCount;
    private final String videoId;
    private final View parentView;
    private final View loadingView;
    private final OnCompleteListener onCompleteListener;
    private YouTubePlayerSupportFragment videoFragment;

    public InitYoutubeVideo(int videoCount, String videoId, View parentView, YouTubePlayerSupportFragment ypsf, OnCompleteListener onCompleteListener) {
        this.videoCount = videoCount;
        this.videoId = videoId;
        this.parentView = parentView;
        this.videoFragment = ypsf;
        this.loadingView = parentView.findViewById(R.id.loadingView);
        this.onCompleteListener = onCompleteListener;
    }

    void initPlayer(String youtubeKey) {

        videoFragment.initialize(youtubeKey,
                new YouTubePlayer.OnInitializedListener() {

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0,
                                                        YouTubeInitializationResult arg1) {
                        loadingView.setVisibility(View.VISIBLE);
                        TextView tv_info = (TextView) loadingView.findViewById(R.id.tv_info);
                        tv_info.setText("Problem loading Video");
                    }

                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        final YouTubePlayer player, boolean restored) {
                        loadingView.setVisibility(View.GONE);
                        try {
                            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                @Override
                                public void onLoading() {

                                }

                                @Override
                                public void onLoaded(String s) {

                                }

                                @Override
                                public void onAdStarted() {

                                }

                                @Override
                                public void onVideoStarted() {

                                }

                                @Override
                                public void onVideoEnded() {
                                    if (onCompleteListener != null) {
                                        try {
                                            onCompleteListener.onVideoComplete(videoCount, videoId);
                                        } catch (Exception e) {
                                            return;
                                        }
                                    }
                                }

                                @Override
                                public void onError(YouTubePlayer.ErrorReason errorReason) {

                                }
                            });
                            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                                    | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
                            player.setShowFullscreenButton(true);
                            player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                                //
                                @Override
                                public void onFullscreen(boolean isFullScreen) {
                                    if (onCompleteListener != null) {
                                        try {
                                            onCompleteListener.onFullscreen(isFullScreen);
                                        } catch (Exception e) {
                                            return;
                                        }
                                    }
                                }
                            });
                            if (!restored) {
                                player.cueVideo(videoId);
                            }
                        } catch (Exception e) {
                            return;
                        }
                    }

                });
    }

    public void init(String youtubeKey) {
        initPlayer(youtubeKey);
    }
}
