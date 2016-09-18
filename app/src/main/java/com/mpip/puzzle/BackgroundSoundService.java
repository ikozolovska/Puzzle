package com.mpip.puzzle;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Matea on 06.09.2016.
 */
public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    private static BackgroundSoundService instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }//met
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.pianoloop);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        instance = this;
    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {
        player.stop();
        instance = null;
    }
    public void onPause() {
        player.pause();
    }

    public void onResume() {
        player.start();
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        instance = null;
    }

    @Override
    public void onLowMemory() {

    }
}