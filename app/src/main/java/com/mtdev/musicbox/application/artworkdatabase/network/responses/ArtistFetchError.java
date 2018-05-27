

package com.mtdev.musicbox.application.artworkdatabase.network.responses;


import com.android.volley.VolleyError;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

import org.json.JSONException;

public interface ArtistFetchError {
    void fetchJSONException(MPDArtist artist, JSONException exception);

    void fetchVolleyError(MPDArtist artist, VolleyError error);
}
