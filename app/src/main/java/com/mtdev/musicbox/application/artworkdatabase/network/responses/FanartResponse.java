
package com.mtdev.musicbox.application.artworkdatabase.network.responses;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

public class FanartResponse {
    public MPDTrack track;
    public String url;
    public byte[] image;
}
