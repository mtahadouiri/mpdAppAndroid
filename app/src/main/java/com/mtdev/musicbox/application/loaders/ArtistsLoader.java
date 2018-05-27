

package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.support.v4.content.Loader;

import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseArtistList;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

import java.lang.ref.WeakReference;
import java.util.List;


public class ArtistsLoader extends Loader<List<MPDArtist>> {

    private MPDResponseArtistList pArtistResponseHandler;

    private boolean mUseAlbumArtists;

    private boolean mUseArtistSort;

    public ArtistsLoader(Context context, boolean useAlbumArtists, boolean useArtistSort) {
        super(context);
        mUseAlbumArtists = useAlbumArtists;
        mUseArtistSort = useArtistSort;
        pArtistResponseHandler = new ArtistResponseHandler(this);
    }


    private static class ArtistResponseHandler extends MPDResponseArtistList {
        private WeakReference<ArtistsLoader> mArtistsLoader;

        private ArtistResponseHandler(ArtistsLoader loader) {
            mArtistsLoader = new WeakReference<>(loader);
        }

        @Override
        public void handleArtists(List<MPDArtist> artistList) {
            ArtistsLoader loader = mArtistsLoader.get();
            if (loader != null) {
                loader.deliverResult(artistList);
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
        if( !mUseAlbumArtists) {
            if(!mUseArtistSort) {
                MPDQueryHandler.getArtists(pArtistResponseHandler);
            } else {
                MPDQueryHandler.getArtistSort(pArtistResponseHandler);
            }
        } else {
            if(!mUseArtistSort) {
                MPDQueryHandler.getAlbumArtists(pArtistResponseHandler);
            } else {
                MPDQueryHandler.getAlbumArtistSort(pArtistResponseHandler);
            }
        }
    }
}
