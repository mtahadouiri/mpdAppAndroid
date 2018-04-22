/*
 *  Copyright (C) 2018 Team Gateship-One
 *  (Hendrik Borghorst & Frederik Luetkes)
 *
 *  The AUTHORS.md file contains a detailed contributors list:
 *  <https://github.com/gateship-one/malp/blob/master/AUTHORS.md>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.mtdev.musicbox.mpdservice.handlers.responsehandler;


import android.os.Bundle;
import android.os.Message;

import java.util.List;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;

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
