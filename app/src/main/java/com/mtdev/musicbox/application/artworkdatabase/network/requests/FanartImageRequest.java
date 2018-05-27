

package com.mtdev.musicbox.application.artworkdatabase.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.FanartResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

public class FanartImageRequest extends MALPRequest<FanartResponse> {

    private final Response.Listener<FanartResponse> mListener;

    private String mURL;
    private MPDTrack mTrack;

    public FanartImageRequest(String url, MPDTrack track, Response.Listener<FanartResponse> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);

        mListener= listener;
        mURL = url;
        mTrack = track;
    }

    @Override
    protected Response<FanartResponse> parseNetworkResponse(NetworkResponse response) {
        FanartResponse fanart = new FanartResponse();
        fanart.url = mURL;
        fanart.image = response.data;
        fanart.track = mTrack;
        return Response.success(fanart,null);
    }

    @Override
    protected void deliverResponse(FanartResponse response) {
        mListener.onResponse(response);
    }

}
