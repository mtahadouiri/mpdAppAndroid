

package com.mtdev.musicbox.application.artworkdatabase.network.responses;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;

public class AlbumImageResponse {
    public MPDAlbum album;
    public String url;
    public byte[] image;
}
