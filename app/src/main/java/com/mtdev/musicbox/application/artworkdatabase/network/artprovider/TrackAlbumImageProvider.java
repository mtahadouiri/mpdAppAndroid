

package com.mtdev.musicbox.application.artworkdatabase.network.artprovider;


import com.android.volley.Response;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.TrackAlbumFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.TrackAlbumImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

public interface TrackAlbumImageProvider {
    void fetchAlbumImage(final MPDTrack track, final Response.Listener<TrackAlbumImageResponse> listener, final TrackAlbumFetchError errorListener);

}
