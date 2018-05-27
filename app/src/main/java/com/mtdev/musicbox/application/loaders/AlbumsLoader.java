
package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.utils.PreferenceHelper;
import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseAlbumList;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;


public class AlbumsLoader extends Loader<List<MPDAlbum>> {

    private MPDResponseAlbumList pAlbumsResponseHandler;

    private String mArtistName;

    private String mAlbumsPath;

    private MPDAlbum.MPD_ALBUM_SORT_ORDER mSortOrder;

    private boolean mUseArtistSort;

    public AlbumsLoader(Context context, String artistName, String albumsPath) {
        super(context);

        pAlbumsResponseHandler = new AlbumResponseHandler(this);

        mArtistName = artistName;
        mAlbumsPath = albumsPath;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mSortOrder = PreferenceHelper.getMPDAlbumSortOrder(sharedPref, context);
        mUseArtistSort =  sharedPref.getBoolean(context.getString(R.string.pref_use_artist_sort_key), context.getResources().getBoolean(R.bool.pref_use_artist_sort_default));
    }


    private static class AlbumResponseHandler extends MPDResponseAlbumList {
        private WeakReference<AlbumsLoader> mAlbumsLoader;

        private AlbumResponseHandler(AlbumsLoader loader) {
            mAlbumsLoader = new WeakReference<>(loader);
        }

        @Override
        public void handleAlbums(List<MPDAlbum> albumList) {
            AlbumsLoader loader = mAlbumsLoader.get();

            if (loader != null) {
                // If artist albums and sort by year is active, resort the list
                if (loader.mSortOrder == MPDAlbum.MPD_ALBUM_SORT_ORDER.DATE && !((null == loader.mArtistName) || loader.mArtistName.isEmpty())) {
                    Collections.sort(albumList, new MPDAlbum.MPDAlbumDateComparator());
                }
                loader.deliverResult(albumList);
            }
        }
    }


    @Override
    public void onStartLoading() {
        forceLoad();
    }

    @Override
    public void onStopLoading() {

    }

    @Override
    public void onForceLoad() {
        if ( (null == mArtistName) || mArtistName.isEmpty() ) {
            if ( null == mAlbumsPath || mAlbumsPath.isEmpty()) {
                MPDQueryHandler.getAlbums(pAlbumsResponseHandler);
            } else {
                MPDQueryHandler.getAlbumsInPath(mAlbumsPath, pAlbumsResponseHandler);
            }
        } else {
            if (!mUseArtistSort) {
                MPDQueryHandler.getArtistAlbums(pAlbumsResponseHandler, mArtistName);
            } else {
                MPDQueryHandler.getArtistSortAlbums(pAlbumsResponseHandler, mArtistName);
            }
        }
    }
}
