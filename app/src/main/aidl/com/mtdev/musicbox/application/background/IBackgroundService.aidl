// IBackgroundService.aidl
package com.mtdev.musicbox.application.background;

// Declare any non-default types here with import statements

interface IBackgroundService {

    void stopStreamingPlayback();
    void startStreamingPlayback();

    int getStreamingStatus();

}
