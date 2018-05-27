

package com.mtdev.musicbox.application.artworkdatabase.network.artprovider;

import com.android.volley.Response;

import com.mtdev.musicbox.application.artworkdatabase.network.responses.ArtistFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.ArtistImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

public interface ArtistImageProvider {
    void fetchArtistImage(final MPDArtist artist, final Response.Listener<ArtistImageResponse> listener, final ArtistFetchError errorListener);
}
