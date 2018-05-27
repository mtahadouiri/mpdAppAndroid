

package com.mtdev.musicbox.application.artworkdatabase.network.artprovider;


import com.android.volley.Response;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.FanartFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.FanartResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.util.List;

public interface FanartProvider {
    void getTrackArtistMBID(final MPDTrack track, final Response.Listener<String> listener, final FanartFetchError errorListener);
    void getArtistFanartURLs(final String mbid, final Response.Listener<List<String>> listener, final FanartFetchError errorListener);
    void getFanartImage(final MPDTrack track, final String url, final Response.Listener<FanartResponse> listener, final  Response.ErrorListener errorListener);
}
