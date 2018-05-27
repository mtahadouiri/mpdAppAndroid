

package com.mtdev.musicbox.mpdservice.mpdprotocol;


import android.util.Log;

import java.util.List;

public class MPDCapabilities {
    private static final String TAG = MPDCapabilities.class.getSimpleName();

    private static final String MPD_TAG_TYPE_MUSICBRAINZ = "musicbrainz";
    private static final String MPD_TAG_TYPE_ALBUMARTIST = "albumartist";
    private static final String MPD_TAG_TYPE_ARTISTSORT = "artistsort";
    private static final String MPD_TAG_TYPE_ALBUMARTISTSORT = "albumartistsort";
    private static final String MPD_TAG_TYPE_DATE = "date";

    private int mMajorVersion;
    private int mMinorVersion;

    private boolean mHasIdle;
    private boolean mHasRangedCurrentPlaylist;
    private boolean mHasSearchAdd;

    private boolean mHasMusicBrainzTags;
    private boolean mHasListGroup;

    private boolean mHasListFiltering;

    private boolean mHasCurrentPlaylistRemoveRange;

    private boolean mHasToggleOutput;

    private boolean mMopidyDetected;

    private boolean mTagAlbumArtist;
    private boolean mTagArtistSort;
    private boolean mTagAlbumArtistSort;
    private boolean mTagDate;

    private boolean mHasPlaylistFind;

    private boolean mHasSeekCurrent;

    public MPDCapabilities(String version, List<String> commands, List<String> tags) {
        String[] versions = version.split("\\.");
        if (versions.length == 3) {
            mMajorVersion = Integer.valueOf(versions[0]);
            mMinorVersion = Integer.valueOf(versions[1]);
        }

        // Only MPD servers greater version 0.14 have ranged playlist fetching, this allows fallback
        if (mMinorVersion > 14 || mMajorVersion > 0) {
            mHasRangedCurrentPlaylist = true;
        } else {
            mHasRangedCurrentPlaylist = false;
        }

        if (mMinorVersion >= 16 || mMajorVersion > 0) {
            mHasCurrentPlaylistRemoveRange = true;
        }

        if (mMinorVersion >= 17 || mMajorVersion > 0) {
            mHasSeekCurrent = true;
        }


        if (mMinorVersion >= 18 || mMajorVersion > 0) {
            mHasToggleOutput = true;
        }

        if (mMinorVersion >= 19 || mMajorVersion > 0) {
            mHasListGroup = true;
            mHasListFiltering = true;
        }



        if (null != commands) {
            mHasIdle = commands.contains(MPDCommands.MPD_COMMAND_START_IDLE);

            mHasSearchAdd = commands.contains(MPDCommands.MPD_COMMAND_ADD_SEARCH_FILES_CMD_NAME);

            mHasPlaylistFind = commands.contains(MPDCommands.MPD_COMMAND_PLAYLIST_FIND);
        }


        if (null != tags) {
            for (String tag : tags) {
                String tagLC = tag.toLowerCase();
                if (tagLC.contains(MPD_TAG_TYPE_MUSICBRAINZ)) {
                    mHasMusicBrainzTags = true;
                    break;
                } else if (tagLC.equals(MPD_TAG_TYPE_ALBUMARTIST)) {
                    mTagAlbumArtist = true;
                } else if (tagLC.equals(MPD_TAG_TYPE_DATE)) {
                    mTagDate = true;
                } else if (tagLC.equals(MPD_TAG_TYPE_ARTISTSORT)) {
                    mTagArtistSort = true;
                } else if (tagLC.equals(MPD_TAG_TYPE_ALBUMARTISTSORT)) {
                    mTagAlbumArtistSort = true;
                }
            }
        }
    }

    public boolean hasIdling() {
        return mHasIdle;
    }

    public boolean hasRangedCurrentPlaylist() {
        return mHasRangedCurrentPlaylist;
    }

    public boolean hasSearchAdd() {
        return mHasSearchAdd;
    }

    public boolean hasListGroup() {
        return mHasListGroup;
    }

    public boolean hasListFiltering() {
        return mHasListFiltering;
    }

    public int getMajorVersion() {
        return mMajorVersion;
    }

    public int getMinorVersion() {
        return mMinorVersion;
    }

    public boolean hasMusicBrainzTags() {
        return mHasMusicBrainzTags;
    }

    public boolean hasCurrentPlaylistRemoveRange() {
        return mHasCurrentPlaylistRemoveRange;
    }

    public boolean hasTagAlbumArtist() {
        return mTagAlbumArtist;
    }

    public boolean hasTagArtistSort() {
        return mTagArtistSort;
    }

    public boolean hasTagAlbumArtistSort() {
        return mTagAlbumArtistSort;
    }

    public boolean hasTagDate() {
        return mTagDate;
    }

    public boolean hasToggleOutput() {
        return mHasToggleOutput;
    }

    public boolean hasPlaylistFind() {
        return mHasPlaylistFind;
    }

    public boolean hasSeekCurrent() {
        return mHasSeekCurrent;
    }

    public String getServerFeatures() {
        return "MPD protocol version: " + mMajorVersion + '.' + mMinorVersion + '\n'
                + "TAGS:" + '\n'
                + "MUSICBRAINZ: " + mHasMusicBrainzTags + '\n'
                + "AlbumArtist: " + mTagAlbumArtist + '\n'
                + "Date: " + mTagDate + '\n'
                + "IDLE support: " + mHasIdle + '\n'
                + "Windowed playlist: " + mHasRangedCurrentPlaylist + '\n'
                + "Fast search add: " + mHasSearchAdd + '\n'
                + "List grouping: " + mHasListGroup + '\n'
                + "List filtering: " + mHasListFiltering + '\n'
                + "Fast ranged currentplaylist delete: " + mHasCurrentPlaylistRemoveRange
                + (mMopidyDetected ? "\nMopidy detected, consider using the real MPD server (www.musicpd.org)!" : "");
    }

    public void enableMopidyWorkaround() {
        Log.w(TAG, "Enabling workarounds for detected Mopidy server");
        mHasListGroup = false;
        mHasListFiltering = false;
        mMopidyDetected = true;

        // Command is listed in "commands" but mopidy returns "not implemented"
        mHasPlaylistFind = false;
    }
}
