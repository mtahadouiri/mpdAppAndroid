

package com.mtdev.musicbox.mpdservice.handlers.responsehandler;


import android.os.Message;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDStatistics;

public abstract class MPDResponseServerStatistics extends MPDResponseHandler {

    public MPDResponseServerStatistics() {

    }

    /**
     * Handle function for the server statistics. This only calls the abstract method
     * which needs to get implemented by the user of this class.
     * @param msg Message object containing a MPDStatistics object
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);


        /* Call album response handler */
        MPDStatistics stats = (MPDStatistics)msg.obj;
        handleStatistic(stats);
    }

    /**
     * Send statistics to the receiving handler
     * @param statistics Object to send
     */
    public void sendServerStatistics(MPDStatistics statistics) {
        Message responseMessage = this.obtainMessage();
        responseMessage.obj = statistics;
        sendMessage(responseMessage);
    }

    /**
     * Abstract method to be implemented by the user of the MPD implementation.
     * This should be a callback for the UI thread and run in the UI thread.
     * This can be used for updating lists of adapters and views.
     * @param statistics Current MPD statistics
     */
    abstract public void handleStatistic(MPDStatistics statistics);
}
