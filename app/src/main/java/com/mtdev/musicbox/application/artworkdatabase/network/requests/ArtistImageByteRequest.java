

package com.mtdev.musicbox.application.artworkdatabase.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.ArtistImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;


public class ArtistImageByteRequest extends MALPRequest<ArtistImageResponse> {

    private final Response.Listener<ArtistImageResponse> mListener;

    private MPDArtist mArtist;
    private String mUrl;

    public ArtistImageByteRequest(String url, MPDArtist artist, Response.Listener<ArtistImageResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        mListener = listener;
        mArtist = artist;
        mUrl = url;
    }

    @Override
    protected Response<ArtistImageResponse> parseNetworkResponse(NetworkResponse response) {
        ArtistImageResponse imageResponse = new ArtistImageResponse();
        imageResponse.artist = mArtist;
        imageResponse.image = response.data;
        imageResponse.url = mUrl;
        return Response.success(imageResponse, null);
    }

    @Override
    protected void deliverResponse(ArtistImageResponse response) {
        mListener.onResponse(response);
    }

}
