

package com.mtdev.musicbox.application.artworkdatabase.network.artprovider;


import com.android.volley.Response;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.AlbumFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.AlbumImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;


public interface AlbumImageProvider {
    void fetchAlbumImage(final MPDAlbum album, final Response.Listener<AlbumImageResponse> listener, final AlbumFetchError errorListener);
}
