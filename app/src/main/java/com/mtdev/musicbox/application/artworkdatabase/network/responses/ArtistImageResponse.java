

package com.mtdev.musicbox.application.artworkdatabase.network.responses;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

public class ArtistImageResponse {
    public MPDArtist artist;
    public String url;
    public byte[] image;
}
