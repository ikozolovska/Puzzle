package com.mpip.puzzle;

/**
 * Created by Matea on 18.09.2016.
 */
public class SoundStatus {
    public static SoundStatus status = null;

    public static boolean shouldBePlaying=true;

    protected SoundStatus() {

    }

    public static SoundStatus getInstance() {
        if(status == null) {
            status = new SoundStatus();
        }
        return status;
    }

    public void setSoundStatus(boolean status) {
        shouldBePlaying = status;
    }
}
