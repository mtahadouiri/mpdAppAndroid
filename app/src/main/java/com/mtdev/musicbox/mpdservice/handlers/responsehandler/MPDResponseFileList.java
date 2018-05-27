

package com.mtdev.musicbox.mpdservice.handlers.responsehandler;


import android.os.Bundle;
import android.os.Message;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;

import java.util.List;

public abstract class MPDResponseFileList extends MPDResponseHandler {
    public static final String EXTRA_WINDOW_START = "windowstart";
    public static final String EXTRA_WINDOW_END = "windowend";

    public MPDResponseFileList() {

    }

    /**
     * Handle function for the track list. This only calls the abstract method
     * which needs to get implemented by the user of this class.
     * @param msg Message object containing a list of MPDTrack items.
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Bundle args = msg.getData();
        int windowStart = msg.getData().getInt(EXTRA_WINDOW_START);
        int windowEnd = msg.getData().getInt(EXTRA_WINDOW_END);

        /* Call album response handler */
        List<MPDFileEntry> trackList = (List<MPDFileEntry>)msg.obj;
        handleTracks(trackList, windowStart, windowEnd);
    }

    public void sendFileList(List<MPDFileEntry> fileList) {
        Message responseMessage = this.obtainMessage();
        responseMessage.obj = fileList;
        sendMessage(responseMessage);
    }

    public void sendFileList(List<MPDFileEntry> fileList, int windowStart, int windowEnd) {
        Message responseMessage = this.obtainMessage();
        responseMessage.obj = fileList;
        Bundle data = new Bundle();
        data.putInt(MPDResponseFileList.EXTRA_WINDOW_START, windowStart);
        data.putInt(MPDResponseFileList.EXTRA_WINDOW_END, windowEnd);
        responseMessage.setData(data);
        sendMessage(responseMessage);
    }

    /**
     * Abstract method to be implemented by the user of the MPD implementation.
     * This should be a callback for the UI thread and run in the UI thread.
     * This can be used for updating lists of adapters and views.
     * @param fileList List of MPDTrack objects containing a list of mpds tracks response.
     */
    abstract public void handleTracks(List<MPDFileEntry> fileList, int windowstart, int windowend);
}
