
package com.mtdev.musicbox.application.artworkdatabase.network.artprovider;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.mtdev.musicbox.application.artworkdatabase.network.MALPRequestQueue;
import com.mtdev.musicbox.application.artworkdatabase.network.requests.AlbumImageByteRequest;
import com.mtdev.musicbox.application.artworkdatabase.network.requests.ArtistImageByteRequest;
import com.mtdev.musicbox.application.artworkdatabase.network.requests.MALPJsonObjectRequest;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.AlbumFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.AlbumImageResponse;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.ArtistFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.ArtistImageResponse;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LastFMManager implements ArtistImageProvider, AlbumImageProvider {
    private static final String TAG = LastFMManager.class.getSimpleName();

    /**
     * Last.fm API url used for requests
     */
    private static final String LAST_FM_API_URL = "http://ws.audioscrobbler.com/2.0/?method=";

    /**
     * API-Key for used for last.fm
     * THIS KEY IS ONLY INTENDED FOR THE USE BY GATESHIP-ONE APPLICATIONS. PLEASE RESPECT THIS.
     */
    private static final String API_KEY = "8de46d96e49e78234f206fd9f21712de";

    /**
     * Constant to request JSON formatted responses
     */
    private static final String LAST_FM_FORMAT_JSON = "&format=json";

    /**
     * Default image download size. Should be around 500px * 500px
     */
    private static final String LAST_FM_REQUESTED_IMAGE_SIZE = "extralarge";

    /**
     * Private {@link RequestQueue} to use for internet requests.
     */
    private RequestQueue mRequestQueue;

    /**
     * Singleton instance
     */
    private static LastFMManager mInstance;

    private LastFMManager(Context context) {
        mRequestQueue = MALPRequestQueue.getInstance(context);
    }

    public static synchronized LastFMManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LastFMManager(context);
        }
        return mInstance;
    }


    /**
     * Fetch an image for an given {@link MPDArtist}. Make sure to provide response and error listener.
     * @param artist Artist to try to get an image for.
     * @param listener ResponseListener that reacts on successful retrieval of an image.
     * @param errorListener Error listener that is called when an error occurs.
     */
    public void fetchArtistImage(final MPDArtist artist, final Response.Listener<ArtistImageResponse> listener, final ArtistFetchError errorListener) {


        String artistURLName = Uri.encode(artist.getArtistName().replaceAll("/", " "));

        getArtistImageURL(artistURLName, response -> {
            try {
                JSONObject artistObj = response.getJSONObject("artist");
                // FIXME optionally get mbid here without aborting the image fetch
                JSONArray images = artistObj.getJSONArray("image");
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.getJSONObject(i);
                    if (image.getString("size").equals(LAST_FM_REQUESTED_IMAGE_SIZE)) {
                        String url = image.getString("#text");
                        if (!url.isEmpty()) {
                            getArtistImage(image.getString("#text"), artist, listener, error -> errorListener.fetchVolleyError(artist, error));
                        } else {
                            errorListener.fetchVolleyError(artist, null);
                        }
                    }
                }
            } catch (JSONException e) {
                errorListener.fetchJSONException(artist, e);
            }
        }, error -> errorListener.fetchVolleyError(artist, error));

    }


    /**
     * Fetches the image URL for the raw image blob.
     * @param artistName Artist name to look for an image
     * @param listener Callback listener to handle the response
     * @param errorListener Callback to handle a fetch error
     */
    private void getArtistImageURL(String artistName, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {


        String url = LAST_FM_API_URL + "artist.getinfo&artist=" + artistName + "&api_key=" + API_KEY + LAST_FM_FORMAT_JSON;
        Log.v(TAG, url);

        MALPJsonObjectRequest jsonObjectRequest = new MALPJsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);

        mRequestQueue.add(jsonObjectRequest);
    }

    /**
     * Raw download for an image
     * @param url Final image URL to download
     * @param artist Artist associated with the image to download
     * @param listener Response listener to receive the image as a byte array
     * @param errorListener Error listener
     */
    private void getArtistImage(String url, MPDArtist artist, Response.Listener<ArtistImageResponse> listener, Response.ErrorListener errorListener) {
        Log.v(LastFMManager.class.getSimpleName(), url);

        Request<ArtistImageResponse> byteResponse = new ArtistImageByteRequest(url, artist, listener, errorListener);

        mRequestQueue.add(byteResponse);
    }


    /**
     * Public interface to get an image for an album.
     * @param album Album to check for an image
     * @param listener Callback to handle the fetched image
     * @param errorListener Callback to handle errors
     */
    @Override
    public void fetchAlbumImage(final MPDAlbum album, final Response.Listener<AlbumImageResponse> listener, final AlbumFetchError errorListener) {
        getAlbumImageURL(album, response -> {
            try {
                JSONObject albumObj = response.getJSONObject("album");
                JSONArray images = albumObj.getJSONArray("image");
                // FIXME optionally get mbid here without aborting the image fetch
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.getJSONObject(i);
                    if (image.getString("size").equals(LAST_FM_REQUESTED_IMAGE_SIZE)) {
                        String url = image.getString("#text");
                        if (!url.isEmpty()) {
                            getAlbumImage(image.getString("#text"), album, listener, error -> errorListener.fetchVolleyError(album, error));
                        } else {
                            errorListener.fetchVolleyError(album, null);
                        }

                    }
                }
            } catch (JSONException e) {
                errorListener.fetchJSONException(album, e);
            }
        }, error -> errorListener.fetchVolleyError(album, error));
    }

    /**
     * Fetches the image URL for the raw image blob.
     * @param album Album to look for an image
     * @param listener Callback listener to handle the response
     * @param errorListener Callback to handle a fetch error
     */
    private void getAlbumImageURL(MPDAlbum album, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String albumName = Uri.encode(album.getName());
        String artistName = Uri.encode(album.getArtistName());

        String url = LAST_FM_API_URL + "album.getinfo&album=" + albumName + "&artist=" + artistName + "&api_key=" + API_KEY + LAST_FM_FORMAT_JSON;
        Log.v(TAG, url);

        MALPJsonObjectRequest jsonObjectRequest = new MALPJsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);

        mRequestQueue.add(jsonObjectRequest);
    }


    /**
     * Raw download for an image
     * @param url Final image URL to download
     * @param album Album associated with the image to download
     * @param listener Response listener to receive the image as a byte array
     * @param errorListener Error listener
     */
    private void getAlbumImage(String url, MPDAlbum album, Response.Listener<AlbumImageResponse> listener, Response.ErrorListener errorListener) {
        Log.v(LastFMManager.class.getSimpleName(), url);

        Request<AlbumImageResponse> byteResponse = new AlbumImageByteRequest(url, album, listener, errorListener);

        mRequestQueue.add(byteResponse);
    }
}
