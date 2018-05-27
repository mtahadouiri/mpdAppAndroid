

package com.mtdev.musicbox.mpdservice.handlers.responsehandler;


import android.os.Message;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;

import java.util.List;

/**
 * Response class for album lists.
 */
public abstract class MPDResponseAlbumList extends MPDResponseHandler {

    public MPDResponseAlbumList() {

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
        List<MPDAlbum> albumList = (List<MPDAlbum>)msg.obj;
        handleAlbums(albumList);
    }

    /**
     * Sends a message to the receiving end of the receiver
     * @param albumList List to send
     */
    public void sendAlbums(List<MPDAlbum> albumList) {
        Message message = obtainMessage();
        message.obj = albumList;
        sendMessage(message);
    }

    /**
     * Abstract method to be implemented by the user of the MPD implementation.
     * This should be a callback for the UI thread and run in the UI thread.
     * This can be used for updating lists of adapters and views.
     * @param albumList List of MPDAlbum objects containing a list of mpds album response.
     */
    abstract public void handleAlbums(List<MPDAlbum> albumList);
}
