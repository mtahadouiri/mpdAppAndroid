

package com.mtdev.musicbox.application.artworkdatabase.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
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
