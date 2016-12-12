package br.com.harbsti.podtube.presentation.presenter.impl;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.widget.SeekBar;

import br.com.harbsti.podtube.presentation.model.Download;
import br.com.harbsti.podtube.presentation.presenter.MediaPlayerPresenter;
import br.com.harbsti.podtube.presentation.view.PlayerView;

/**
 * Created by marcosharbs on 09/12/16.
 */

public class MediaPlayerPresenterImpl implements MediaPlayerPresenter {

    private PlayerView playerView;
    private MediaPlayer mediaPlayer;
    private CountDownTimer trackMediaPlayer;
    private Download download;

    public MediaPlayerPresenterImpl(PlayerView playerView) {
        this.playerView = playerView;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onPause() {}

    @Override
    public void onResume() {}

    @Override
    public void play(Download download) {
        this.download = download;
        stopMediaPlayer();
        cancelTracker();
        createMediaPlayer();
        createTracker();
        playerView.onShowPlayer();
    }

    @Override
    public void pausePlayer() {
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }
        cancelTracker();
    }

    @Override
    public void playPlayer() {
        if(mediaPlayer == null){
            play(download);
        }else{
            mediaPlayer.start();
            mediaPlayer.seekTo(playerView.getPlayerTrack().getProgress());
            cancelTracker();
            createTracker();
        }
    }

    @Override
    public void stopPlayer() {
        stopMediaPlayer();
        cancelTracker();
        mediaPlayer = null;
        playerView.onHidePlayer();
    }

    private void createMediaPlayer() {
        mediaPlayer = MediaPlayer.create(playerView.getContext(), Uri.parse(download.mp3File()));
        mediaPlayer.setOnCompletionListener(getOnCompletionListener());
        mediaPlayer.start();
        playerView.getPlayerTrack().setMax(mediaPlayer.getDuration());
        playerView.getPlayerTrack().setOnSeekBarChangeListener(getSeekBarListener());
    }

    private void cancelTracker() {
        if(trackMediaPlayer != null){
            trackMediaPlayer.cancel();
        }
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }

    private void createTracker() {
        trackMediaPlayer = new CountDownTimer(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition(), 200) {
            @Override
            public void onTick(long l) {
                if(mediaPlayer != null) {
                    playerView.getPlayerTrack().setProgress((int) (mediaPlayer.getDuration() - l));
                }
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    private MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayerIn) {
                mediaPlayer = null;
                cancelTracker();
            }
        };
    }

    private SeekBar.OnSeekBarChangeListener getSeekBarListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(seekBar.getProgress());
                    cancelTracker();
                    createTracker();
                }
            }
        };
    }

}
