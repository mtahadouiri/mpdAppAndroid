

package com.mtdev.musicbox.application.artworkdatabase.network.responses;


import com.android.volley.VolleyError;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;

import org.json.JSONException;

public interface AlbumFetchError {
    void fetchJSONException(MPDAlbum album, JSONException exception);

    void fetchVolleyError(MPDAlbum album, VolleyError error);
}
