

package com.mtdev.musicbox.application.artworkdatabase.network.responses;


import com.android.volley.VolleyError;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import org.json.JSONException;

public interface TrackAlbumFetchError {
    void fetchJSONException(MPDTrack track, JSONException exception);

    void fetchVolleyError(MPDTrack track, VolleyError error);
}
