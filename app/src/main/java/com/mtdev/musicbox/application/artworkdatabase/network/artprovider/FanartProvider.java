/*
 *  Copyright (C) 2018 Team Gateship-One
 *  (Hendrik Borghorst & Frederik Luetkes)
 *
 *  The AUTHORS.md file contains a detailed contributors list:
 *  <https://github.com/gateship-one/malp/blob/master/AUTHORS.md>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.mtdev.musicbox.application.artworkdatabase.network.artprovider;


import com.android.volley.Response;

import com.mtdev.musicbox.application.artworkdatabase.network.responses.FanartFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.FanartResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.util.List;

public interface FanartProvider {
    void getTrackArtistMBID(final MPDTrack track, final Response.Listener<String> listener, final FanartFetchError errorListener);
    void getArtistFanartURLs(final String mbid, final Response.Listener<List<String>> listener, final FanartFetchError errorListener);
    void getFanartImage(final MPDTrack track, final String url, final Response.Listener<FanartResponse> listener, final  Response.ErrorListener errorListener);
}
