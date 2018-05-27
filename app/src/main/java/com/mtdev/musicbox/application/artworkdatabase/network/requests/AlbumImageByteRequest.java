
package com.mtdev.musicbox.application.artworkdatabase.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.AlbumImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;


public class AlbumImageByteRequest extends MALPRequest<AlbumImageResponse> {

    private final Response.Listener<AlbumImageResponse> mListener;

    private MPDAlbum mAlbum;
    private String mUrl;


    public AlbumImageByteRequest(String url, MPDAlbum album, Response.Listener<AlbumImageResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        mListener = listener;
        mAlbum = album;
        mUrl = url;
    }

    @Override
    protected Response<AlbumImageResponse> parseNetworkResponse(NetworkResponse response) {
        AlbumImageResponse imageResponse = new AlbumImageResponse();
        imageResponse.album = mAlbum;
        imageResponse.image = response.data;
        imageResponse.url = mUrl;
        return Response.success(imageResponse, null);
    }

    @Override
    protected void deliverResponse(AlbumImageResponse response) {
        mListener.onResponse(response);
    }

}
