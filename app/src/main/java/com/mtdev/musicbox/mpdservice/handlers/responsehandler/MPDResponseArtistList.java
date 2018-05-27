
package com.mtdev.musicbox.mpdservice.handlers.responsehandler;


import android.os.Message;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

import java.util.List;

/**
 * Response class for album lists.
 */
public abstract class MPDResponseArtistList extends MPDResponseHandler {

    public MPDResponseArtistList() {

    }

    /**
     * Handle function for the album list. This only calls the abstract method
     * which needs to get implemented by the user of this class.
     * @param msg Message object containing a list of MPDAlbum items.
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        /* Call album response handler */
        List<MPDArtist> artistList = (List<MPDArtist>)msg.obj;
        handleArtists(artistList);
    }

    /**
     * Sends the artist list to the receiving handler
     * @param artistList List to send
     */
    public void sendArtists(List<MPDArtist> artistList) {
        Message message = obtainMessage();
        message.obj = artistList;
        sendMessage(message);
    }

    /**
     * Abstract method to be implemented by the user of the MPD implementation.
     * This should be a callback for the UI thread and run in the UI thread.
     * This can be used for updating lists of adapters and views.
     * @param artistList List of MPDAlbum objects containing a list of mpds album response.
     */
    abstract public void handleArtists(List<MPDArtist> artistList);
}
