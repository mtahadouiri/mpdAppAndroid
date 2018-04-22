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

package com.mtdev.musicbox.application.artworkdatabase.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import com.mtdev.musicbox.application.artworkdatabase.network.responses.TrackAlbumImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.TrackAlbumImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;


public class TrackAlbumImageByteRequest extends MALPRequest<TrackAlbumImageResponse> {

    private final Response.Listener<TrackAlbumImageResponse> mListener;

    private MPDTrack mTrack;
    private String mUrl;


    public TrackAlbumImageByteRequest(String url, MPDTrack track, Response.Listener<TrackAlbumImageResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        mListener = listener;
        mTrack = track;
        mUrl = url;
    }

    @Override
    protected Response<TrackAlbumImageResponse> parseNetworkResponse(NetworkResponse response) {
        TrackAlbumImageResponse imageResponse = new TrackAlbumImageResponse();
        imageResponse.track = mTrack;
        imageResponse.image = response.data;
        imageResponse.url = mUrl;
        return Response.success(imageResponse, null);
    }

    @Override
    protected void deliverResponse(TrackAlbumImageResponse response) {
        mListener.onResponse(response);
    }

}
