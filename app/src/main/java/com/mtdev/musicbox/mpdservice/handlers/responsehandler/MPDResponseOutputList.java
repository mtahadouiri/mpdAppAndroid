

package com.mtdev.musicbox.mpdservice.handlers.responsehandler;


import android.os.Message;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDOutput;

import java.util.List;

public abstract class MPDResponseOutputList extends MPDResponseHandler {

    public MPDResponseOutputList() {

    }

    /**
     * Handle function for the track list. This only calls the abstract method
     * which needs to get implemented by the user of this class.
     * @param msg Message object containing a list of MPDTrack items.
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        /* Call album response handler */
        List<MPDOutput> outputList = (List<MPDOutput>)msg.obj;
        handleOutputs(outputList);
    }

    /**
     * Sends the list of outputs to the receiving handler looper
     * @param outputList
     */
    public void sendOutputs(List<MPDOutput> outputList) {
        Message responseMessage = this.obtainMessage();
        responseMessage.obj = outputList;
        sendMessage(responseMessage);
    }

    /**
     * Abstract method to be implemented by the user of the MPD implementation.
     * This should be a callback for the UI thread and run in the UI thread.
     * This can be used for updating lists of adapters and views.
     * @param outputList List of MPDOutput objects containing a list of available MPD outputs
     */
    abstract public void handleOutputs(List<MPDOutput> outputList);
}
