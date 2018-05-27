

package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.support.v4.content.Loader;

import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseFileList;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Loader class for albumtracks and artist album tracks
 */
public class AlbumTracksLoader extends Loader<List<MPDFileEntry>> {
    private static final String TAG = AlbumTracksLoader.class.getSimpleName();
    /**
     * Response handler used for the asynchronous callback of the networking thread
     */
    private MPDResponseFileList pTrackResponseHandler;

    /**
     * Artist name of this album. Can be left empty
     */
    private String mArtistName;

    private String mArtistSortName;
    /**
     * Name of the album to retrieve
     */
    private String mAlbumName;

    private String mAlbumMBID;

    private boolean mUseArtistSort;

    /**
     * Creates the loader that retrieves the information from the MPD server
     *
     * @param context    Context used
     * @param album album for which tracks should be loaded
     */
    public AlbumTracksLoader(Context context, MPDAlbum album, boolean useArtistSort) {
        super(context);

        // Create a new Handler for asynchronous callback
        pTrackResponseHandler = new TrackResponseHandler(this);

        // Set the album properties
        mArtistName = album.getArtistName();
        mArtistSortName = album.getArtistSortName();
        mAlbumName = album.getName();
        mAlbumMBID = album.getMBID();

        mUseArtistSort = useArtistSort;
    }


    /**
     * Private class for the response handler.
     */
    private static class TrackResponseHandler extends MPDResponseFileList {
        private WeakReference<AlbumTracksLoader> mAlbumTracksLoader;

        private TrackResponseHandler(AlbumTracksLoader loader) {
            mAlbumTracksLoader = new WeakReference<>(loader);
        }


        @Override
        public void handleTracks(List<MPDFileEntry> trackList, int start, int end) {
            AlbumTracksLoader loader = mAlbumTracksLoader.get();

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
     * Start the actual loading process. Check if an artistname was provided.
     * If fetch the artistalbumtracks otherwise fetch all tracks for a specific album.
     */
    @Override
    public void onForceLoad() {
        if ((null == mArtistName) || mArtistName.equals("")) {
            MPDQueryHandler.getAlbumTracks(pTrackResponseHandler, mAlbumName, mAlbumMBID);
        } else {
            if (mUseArtistSort && !mArtistSortName.isEmpty()) {
                MPDQueryHandler.getArtistSortAlbumTracks(pTrackResponseHandler, mAlbumName, mArtistSortName, mAlbumMBID);
            } else {
                MPDQueryHandler.getArtistAlbumTracks(pTrackResponseHandler, mAlbumName, mArtistName, mAlbumMBID);
            }
        }
    }
}
