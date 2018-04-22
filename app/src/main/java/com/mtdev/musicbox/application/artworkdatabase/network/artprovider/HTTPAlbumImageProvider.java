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


import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import com.mtdev.musicbox.application.artworkdatabase.network.requests.TrackAlbumImageByteRequest;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.TrackAlbumFetchError;
import com.mtdev.musicbox.application.artworkdatabase.network.responses.TrackAlbumImageResponse;
import com.mtdev.musicbox.application.utils.FormatHelper;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;
import com.mtdev.musicbox.application.utils.FormatHelper;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

public class HTTPAlbumImageProvider implements TrackAlbumImageProvider {
    private static final String TAG = HTTPAlbumImageProvider.class.getSimpleName();

    /**
     * Filename combinations used if only a directory is specified
     */
    private static final String[] COVER_FILENAMES = {"cover","folder","Cover","Folder"};

    /**
     * File extensions tried for all filenames
     */
    private static final String[] COVER_FILEEXTENSIIONS = {"png","jpg","jpeg","PNG","JPG","JPEG"};

    /**
     * Singleton instance
     */
    private static HTTPAlbumImageProvider mInstance;

    /**
     * Regex used for downloading
     */
    private static String mRegex;

    /**
     * {@link RequestQueue} used for downloading images. Separate queue for this provider
     * because no request limitations are necessary
     */
    private RequestQueue mRequestQueue;


    private HTTPAlbumImageProvider(Context context) {
        // Don't use MALPRequestQueue because we do not need to limit the load on the local server
        Network network = new BasicNetwork(new HurlStack());
        // 10MB disk cache
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024 * 10);

        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    }

    public static synchronized HTTPAlbumImageProvider getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HTTPAlbumImageProvider(context);
        }

        return mInstance;
    }

    public void setRegex(String regex) {
        mRegex = regex;
    }

    public String getRegex() {
        return mRegex;
    }

    public boolean getActive() {
        if (mRegex==null || mRegex.isEmpty()) {
            return false;
        }
        return true;
    }

    private String resolveRegex(String path) {
        String result;

        result = mRegex.replaceAll("%f", FormatHelper.encodeURLUnsafeCharacters(path));
        result = result.replaceAll("%d", FormatHelper.encodeURLUnsafeCharacters(FormatHelper.getDirectoryFromPath(path)));

        return result;
    }

    @Override
    public void fetchAlbumImage(final MPDTrack track, Response.Listener<TrackAlbumImageResponse> listener, final TrackAlbumFetchError errorListener) {

        String url = resolveRegex(track.getPath());


        // Check if URL ends with a file or directory
        if (url.endsWith("/")) {
            final HTTPMultiRequest multiRequest = new HTTPMultiRequest(track, errorListener);
            // Directory check all pre-defined files
            for(String filename : COVER_FILENAMES) {
                for (String fileextension: COVER_FILEEXTENSIIONS) {
                    String fileURL = url + filename + '.' + fileextension;
                    getAlbumImage(fileURL, track, listener, multiRequest::increaseFailure);
                }
            }
        } else {
            // File, just check the file
            getAlbumImage(url, track, listener, error -> errorListener.fetchVolleyError(track,error));
        }
    }

    /**
     * Raw download for an image
     * @param url Final image URL to download
     * @param track Track associated with the image to download
     * @param listener Response listener to receive the image as a byte array
     * @param errorListener Error listener
     */
    private void getAlbumImage(String url, MPDTrack track, Response.Listener<TrackAlbumImageResponse> listener, Response.ErrorListener errorListener) {
        Request<TrackAlbumImageResponse> byteResponse = new TrackAlbumImageByteRequest(url, track, listener, errorListener);
        mRequestQueue.add(byteResponse);
    }

    private class HTTPMultiRequest {
        private int mFailureCount;
        private TrackAlbumFetchError mErrorListener;
        private MPDTrack mTrack;

        public HTTPMultiRequest(MPDTrack track, TrackAlbumFetchError errorListener) {
            mTrack = track;
            mErrorListener = errorListener;
        }

        public synchronized void increaseFailure(VolleyError error) {
            mFailureCount++;
            if ( mFailureCount == COVER_FILENAMES.length * COVER_FILEEXTENSIIONS.length) {
                mErrorListener.fetchVolleyError(mTrack, error);
            }
        }
    }
}
