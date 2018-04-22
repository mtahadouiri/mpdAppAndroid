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

package com.mtdev.musicbox.application.artworkdatabase;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

/**
 * Simple LRU-based caching for album & artist images. This could reduce CPU usage
 * for the cost of memory usage by caching decoded {@link Bitmap} objects in a {@link LruCache}.
 */
public class BitmapCache {
    private static final String TAG = BitmapCache.class.getSimpleName();

    private static final int mMaxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);

    /**
     * Maximum size of the cache in kilobytes
     */
    private static final int mCacheSize = mMaxMemory / 4;

    /**
     * Hash prefix for album images
     */
    private static final String ALBUM_PREFIX = "A_";

    /**
     * Hash prefix for artist images
     */
    private static final String ARTIST_PREFIX = "B_";

    /**
     * Private cache instance
     */
    private LruCache<String, Bitmap> mCache;

    /**
     * Singleton instance
     */
    private static BitmapCache mInstance;

    private BitmapCache() {
        mCache = new LruCache<String, Bitmap>(mCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }

        };
    }

    public static synchronized BitmapCache getInstance() {
        if (mInstance == null) {
            mInstance = new BitmapCache();
        }
        return mInstance;
    }

    /**
     * Tries to get an album image from the cache
     *
     * @param album Album object to try
     * @return Bitmap if cache hit, null otherwise
     */
    public synchronized Bitmap requestAlbumBitmap(MPDAlbum album) {
        Bitmap bitmap = mCache.get(getAlbumHash(album));
        return bitmap;
    }

    /**
     * Puts an album image to the cache
     *
     * @param album Album object to use for cache key
     * @param bm    Bitmap to store in cache
     */
    public synchronized void putAlbumBitmap(MPDAlbum album, Bitmap bm) {
        if (bm != null) {
            mCache.put(getAlbumHash(album), bm);
        }
    }

    /**
     * Tries to get an album image from the cache
     *
     * @param albumName  Album name used as key
     * @param artistName Albumartist name used as key
     * @return Bitmap if cache hit, null otherwise
     */
    public synchronized Bitmap requestAlbumBitmap(String albumName, String artistName) {
        return mCache.get(getAlbumHash(albumName, artistName));
    }

    /**
     * Puts an album image to the cache
     *
     * @param albumName  Album name used as key
     * @param artistName Albumartist name used as key
     * @param bm         Bitmap to store in cache
     */
    public synchronized void putAlbumBitmap(String albumName, String artistName, Bitmap bm) {
        if (bm != null) {
            mCache.put(getAlbumHash(albumName, artistName), bm);
        }
    }

    /**
     * Tries to get an album image from the cache
     *
     * @param mbid MBID used as key
     * @return Bitmap if cache hit, null otherwise
     */
    public synchronized Bitmap requestAlbumBitmapMBID(final String mbid) {
        return mCache.get(getAlbumHashMBID(mbid));
    }

    /**
     * Puts an album image to the cache
     *
     * @param mbid MBID used as key
     * @param bm   Bitmap to store in cache
     */
    public synchronized void putAlbumBitmapMBID(String mbid, Bitmap bm) {
        if (bm != null) {
            mCache.put(getAlbumHashMBID(mbid), bm);
        }
    }

    /**
     * Private hash method for cache key
     *
     * @param album Album to calculate the key from
     * @return Hash string for cache key
     */
    private String getAlbumHash(MPDAlbum album) {
        String hashString = "ALBUM_";
        final String albumMBID = album.getMBID();

        if (!albumMBID.isEmpty()) {
            hashString += albumMBID;
            return hashString;
        }

        final String albumArtist = album.getArtistName();
        final String albumName = album.getName();

        hashString += albumArtist + '_' + albumName;
        return hashString;
    }

    /**
     * Private hash method for cache key
     *
     * @param albumName  Album name to calculate key from
     * @param artistName Album artist name to calculate key from
     * @return Hash string for cache key
     */
    private String getAlbumHash(String albumName, String artistName) {
        return ALBUM_PREFIX + artistName + '_' + albumName;
    }

    /**
     * Private hash method for cache key
     *
     * @param mbid MBID used as cache key
     * @return Hash string for cache key
     */
    private String getAlbumHashMBID(String mbid) {
        return ALBUM_PREFIX + mbid;
    }

    /*
     * Begin of artist image handling
     */

    /**
     * Tries to get an artist image from the cache
     *
     * @param artist Artist object to check in cache
     * @return Bitmap if cache hit, null otherwise
     */
    public synchronized Bitmap requestArtistImage(MPDArtist artist) {
        return mCache.get(getArtistHash(artist));
    }

    /**
     * Puts an artist image to the cache
     *
     * @param artist Artist used as cache key
     * @param bm     Bitmap to store in cache
     */
    public synchronized void putArtistImage(MPDArtist artist, Bitmap bm) {
        if (bm != null) {
            mCache.put(getArtistHash(artist), bm);
        }
    }

    /**
     * Private hash method for cache key
     *
     * @param artist Artist used as cache key
     * @return Hash string for cache key
     */
    private String getArtistHash(MPDArtist artist) {
        String hashString = ARTIST_PREFIX;
        if (artist.getMBIDCount() > 0) {
            hashString += artist.getMBID(0);
            return hashString;
        }

        hashString += artist.getArtistName();
        return hashString;
    }

    /**
     * Debug method to provide performance evaluation metrics
     */
    private void printUsage() {
        Log.v(TAG, "Cache usage: " + ((mCache.size() * 100) / mCache.maxSize()) + '%');
        int missCount = mCache.missCount();
        int hitCount = mCache.hitCount();
        if (missCount > 0) {
            Log.v(TAG, "Cache hit count: " + hitCount + " miss count: " + missCount + " Miss rate: " + ((hitCount * 100) / missCount)+ '%');
        }
    }
}
