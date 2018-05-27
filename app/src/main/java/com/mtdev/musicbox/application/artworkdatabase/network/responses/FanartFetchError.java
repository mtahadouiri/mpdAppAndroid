

package com.mtdev.musicbox.application.artworkdatabase.network.responses;


import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

public interface FanartFetchError {
    void imageListFetchError();
    void fanartFetchError(MPDTrack track);
}
