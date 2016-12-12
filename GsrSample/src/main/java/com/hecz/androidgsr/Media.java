package com.hecz.androidgsr;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

/**
 * Created by Marek on 21.3.2016.
 */
public class Media {

    public static final int MSG_AUDIO_PREPARED = 101;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer = new MediaPlayer();
    private volatile boolean threadIsRunning = false;
    private volatile boolean runThread = true;
    private SoundPool sp = null;
    private int streamID = 0;
    private int soundID;
    private Handler handler;
//    private enum Action {
//        nothing,
//        playing,
//        recording
//    }
//    private Action action = Action.nothing;
    private boolean playing = false;
    private boolean recording = false;

    public Media() {
    }

    public void playStart() {
        playing = true;
        streamID = sp.play(soundID, 1, 1, 1, 0, 1);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    //* set playbackMute fo microphone or speaker
    public void setMediaMute(boolean mute) {
        if (playing) {
            if (mute) {
                sp.setVolume(soundID, 0, 0);
            } else {
                sp.setVolume(soundID, 1, 1);
            }
        } //else if (action == Action.recording) {
            //mRecorder.set
        //}
    }

    public void setPlaybackPause(boolean pause) {
        if (playing) {
            if (pause) {
                sp.pause(soundID);
            } else {
                sp.resume(soundID);
            }
        }
    }

    public void setPlaybackFast(boolean fast) {
        if (playing) {
            if (fast) {
                sp.setRate(soundID, 2);
            } else {
                sp.setRate(soundID, 1);
            }
        }
    }

    public void playPrepare(String fileName) {
//        try {
//            mPlayer.setDataSource(fileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            AudioAttributes audioAttrib = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_GAME)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                    .build();
//            sp = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(6).build();
//        } else {
            sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
//        }
        soundID = sp.load(fileName, 1);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Message msg = handler.obtainMessage(MSG_AUDIO_PREPARED);
                handler.sendMessage(msg);
            }
        });
    }

    public void playStop() {
        if (playing) {
            playing = false;
            mPlayer.stop();
        }
    }

    //* "/mnt/sdcard/GSR History/audio.3gp"
    public void recordStart(String fileName) {
        recording = true;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(fileName);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recordStop() {
        if (recording) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder = null;
            recording = false;
        }
    }
}
