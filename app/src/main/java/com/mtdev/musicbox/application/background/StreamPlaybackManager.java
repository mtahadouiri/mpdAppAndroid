
package com.mtdev.musicbox.application.background;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;


public class StreamPlaybackManager {
    private static final String TAG = StreamPlaybackManager.class.getSimpleName();
    private MediaPlayer mPlayer;

    private BackgroundService mService;

    private PreparedListener mPreparedListener;
    private CompletionListener mCompletionListener;
    private ErrorListener mErrorListener;

    private String mSource;

    private boolean mPreparing;

    public StreamPlaybackManager(BackgroundService service) {
        mService = service;

        mPreparedListener = new PreparedListener();
        mCompletionListener = new CompletionListener();
        mErrorListener = new ErrorListener();
    }

    public void playURL(String url) {
        mSource = url;

        if (null != mPlayer) {
            mPlayer.release();
        }

        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(mPreparedListener);
        mPlayer.setOnCompletionListener(mCompletionListener);
        mPlayer.setOnErrorListener(mErrorListener);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        startPlayback();
    }

    private void startPlayback() {
        try {
            mPlayer.setDataSource(mSource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayer.prepareAsync();
        broadcastStatusUpdate(BackgroundService.STREAMING_STATUS.BUFFERING);
    }

    public void stop() {
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        broadcastStatusUpdate(BackgroundService.STREAMING_STATUS.STOPPED);
    }

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public void setVolume(float volume) {
        if (mPlayer != null) {
            mPlayer.setVolume(volume,volume);
        }
    }

    private void broadcastStatusUpdate(BackgroundService.STREAMING_STATUS status) {
        Intent intent = new Intent();
        intent.setAction(BackgroundService.ACTION_STREAMING_STATUS_CHANGED);
        intent.putExtra(BackgroundService.INTENT_EXTRA_STREAMING_STATUS, status.ordinal());
        mService.sendBroadcast(intent);
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.v(TAG,"Prepared, start playback");
            mp.setWakeMode(mService.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mp.start();
            mService.onStreamPlaybackStart();
            broadcastStatusUpdate(BackgroundService.STREAMING_STATUS.PLAYING);
        }
    }

    private class ErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mPlayer.release();
            mPlayer = null;
            broadcastStatusUpdate(BackgroundService.STREAMING_STATUS.STOPPED);
            return true;
        }
    }

    private class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    }

    public BackgroundService.STREAMING_STATUS getStreamingStatus() {
        if(null != mPlayer) {
            if(mPlayer.isPlaying()) {
                return BackgroundService.STREAMING_STATUS.PLAYING;
            } else {
                return BackgroundService.STREAMING_STATUS.BUFFERING;
            }
        }
        return BackgroundService.STREAMING_STATUS.STOPPED;
    }
}
