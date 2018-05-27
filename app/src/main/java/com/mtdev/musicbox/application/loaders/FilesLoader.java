

package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;
import java.util.List;

import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseFileList;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;

/**
 * Loads a list of files, directories and playlists from the MPDQueryHandler
 */
public class FilesLoader extends Loader<List<MPDFileEntry>> {
    private FilesResponseHandler mFilesResponseHandler;

    /**
     * Path to request file entries from
     */
    String mPath;

    public FilesLoader(Context context, String path) {
        super(context);
        mPath = path;

        // Response handler for receiving the file list asynchronously
        mFilesResponseHandler = new FilesResponseHandler(this);
    }


    /**
     * Delivers the results to the GUI thread
     */
    private static class FilesResponseHandler extends MPDResponseFileList {
        private WeakReference<FilesLoader> mFilesLoader;

        private FilesResponseHandler(FilesLoader loader) {
            mFilesLoader = new WeakReference<>(loader);
        }

        @Override
        public void handleTracks(List<MPDFileEntry> fileList, int start, int end) {
            FilesLoader loader = mFilesLoader.get();

            if (loader != null) {
                loader.deliverResult(fileList);
            }
        }
    }


    /**
     * Start the loader
     */
    @Override
    public void onStartLoading() {
        forceLoad();
    }


    /**
     * Stop the loader
     */
    @Override
    public void onStopLoading() {

    }

    /**
     * Requests the file list from the MPDQueryHandler, it will respond asynchronously
     */
    @Override
    public void onForceLoad() {
        MPDQueryHandler.getFiles(mFilesResponseHandler,mPath);
    }
}
