

package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.support.v4.content.Loader;

import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseFileList;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Loader class for albumtracks and artist album tracks
 */
public class PlaylistTrackLoader extends Loader<List<MPDFileEntry>> {
    /**
     * Response handler used for the asynchronous callback of the networking thread
     */
    private MPDResponseFileList pTrackResponseHandler;

    /**
     * Name/Path of the playlist to get. If empty, current playlist is used.
     */
    private String mPlaylistPath;



    /**
     * Creates the loader that retrieves the information from the MPD server
     * @param context Context used
     * @param playlistPath Name of the playlist to fetch. If empty it will use the current one.
     */
    public PlaylistTrackLoader(Context context, String playlistPath) {
        super(context);

        // Create a new Handler for asynchronous callback
        pTrackResponseHandler = new TrackResponseHandler(this);

        // Set the playlist properties
        mPlaylistPath = playlistPath;
    }


    /**
     * Private class for the response handler.
     */
    private static class TrackResponseHandler extends MPDResponseFileList {
        private WeakReference<PlaylistTrackLoader> mPlaylistTrackLoader;

        private TrackResponseHandler(PlaylistTrackLoader loader) {
            mPlaylistTrackLoader = new WeakReference<>(loader);
        }

        @Override
        public void handleTracks(List<MPDFileEntry> trackList, int start, int end) {
            PlaylistTrackLoader loader = mPlaylistTrackLoader.get();

            if (loader != null) {
                loader.deliverResult(trackList);
            }
        }
    }


    /**
     * Starts the loading process
     */
    @Override
    public void onStartLoading() {
        forceLoad();
    }

    /**
     * When the loader is stopped
     */
    @Override
    public void onStopLoading() {

    }


    /**
     * Start the actual laoding process. Check if an playlistpath is provided.
     * If not it will just fetch the current playlist.
     */
    @Override
    public void onForceLoad() {
        if ( (null == mPlaylistPath) || mPlaylistPath.equals("")) {
            MPDQueryHandler.getCurrentPlaylist(pTrackResponseHandler);
        } else {
            MPDQueryHandler.getSavedPlaylist(pTrackResponseHandler, mPlaylistPath);
        }
    }
}
