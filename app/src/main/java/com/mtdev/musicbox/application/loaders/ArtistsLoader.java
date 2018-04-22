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

package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;
import java.util.List;

import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseArtistList;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;


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
