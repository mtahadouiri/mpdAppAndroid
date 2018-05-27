
package com.mtdev.musicbox.mpdservice.mpdprotocol;


import android.util.Log;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDCurrentStatus;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDDirectory;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDOutput;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDPlaylist;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDStatistics;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDCurrentStatus;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDDirectory;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDOutput;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDPlaylist;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDStatistics;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MPDResponseParser {
    private static final String TAG = MPDResponseParser.class.getSimpleName();

    /**
     * Parses the return of MPD when a list of albums was requested.
     *
     * @return List of MPDAlbum objects
     * @throws MPDException if an error from MPD was received during reading
     */
    static ArrayList<MPDAlbum> parseMPDAlbums(final MPDConnection connection) throws MPDException {
        ArrayList<MPDAlbum> albumList = new ArrayList<>();
        if (!connection.isConnected()) {
            return albumList;
        }
        /* Parse the MPD response and create a list of MPD albums */

        String albumName;

        MPDAlbum tempAlbum = null;

        String responseString = connection.readLine();
        while (responseString != null && !responseString.startsWith("OK")) {
            /* Check if the response is an album */
            if (responseString.startsWith(MPDResponses.MPD_RESPONSE_ALBUM_NAME)) {
                /* We found an album, add it to the list. */
                if (null != tempAlbum) {
                    albumList.add(tempAlbum);
                }
                albumName = responseString.substring(MPDResponses.MPD_RESPONSE_ALBUM_NAME.length());
                tempAlbum = new MPDAlbum(albumName);
            }
            if (tempAlbum != null) {
                if (responseString.startsWith(MPDResponses.MPD_RESPONSE_ALBUM_MBID)) {
                    tempAlbum.setMBID(responseString.substring(MPDResponses.MPD_RESPONSE_ALBUM_MBID.length()));
                } else if (responseString.startsWith(MPDResponses.MPD_RESPONSE_ALBUMARTIST_NAME)) {
                /* Check if the responseString is a album artist. */
                    tempAlbum.setArtistName(responseString.substring(MPDResponses.MPD_RESPONSE_ALBUMARTIST_NAME.length()));
                } else if (responseString.startsWith(MPDResponses.MPD_RESPONSE_ALBUMARTIST_SORT_NAME)) {
                /* Check if the responseString is a album artist. */
                    tempAlbum.setArtistSortName(responseString.substring(MPDResponses.MPD_RESPONSE_ALBUMARTIST_SORT_NAME.length()));
                } else if (responseString.startsWith(MPDResponses.MPD_RESPONSE_DATE)) {
                    // Try to parse Date
                    String dateString = responseString.substring(MPDResponses.MPD_RESPONSE_DATE.length());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy");
                    try {
                        tempAlbum.setDate(format.parse(dateString));
                    } catch (ParseException e) {
                        Log.w(TAG, "Error parsing date: " + dateString);
                    }
                }
            }
            responseString = connection.readLine();
        }

        /* Because of the loop structure the last album has to be added because no
        "ALBUM:" is sent anymore.
         */
        if (null != tempAlbum) {
            albumList.add(tempAlbum);
        }

        // Sort the albums for later sectioning.
        Collections.sort(albumList);
        return albumList;
    }

    /**
     * Parses the return stream of MPD when a list of artists was requested.
     *
     * @return List of MPDArtists objects
     * @throws MPDException if an error from MPD was received during reading
     */
    static ArrayList<MPDArtist> parseMPDArtists(final MPDConnection connection, final boolean hasMusicBrainz, final boolean hasListGroup) throws MPDException {
        ArrayList<MPDArtist> artistList = new ArrayList<>();
        if (!connection.isConnected()) {
            return artistList;
        }

        /* Parse MPD artist return values and create a list of MPDArtist objects */
        String response = connection.readLine();

        /* Artist properties */
        String artistName;
        String artistMBID;

        MPDArtist tempArtist = null;

        while (response != null && !response.startsWith("OK")) {

            // Handle new artist entry
            if (response.startsWith(MPDResponses.MPD_RESPONSE_ARTIST_NAME)) {
                if (null != tempArtist) {
                    artistList.add(tempArtist);
                }
                artistName = response.substring(MPDResponses.MPD_RESPONSE_ARTIST_NAME.length());
                tempArtist = new MPDArtist(artistName);
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ALBUMARTIST_NAME)) {
                if (null != tempArtist) {
                    artistList.add(tempArtist);
                }
                artistName = response.substring(MPDResponses.MPD_RESPONSE_ALBUMARTIST_NAME.length());
                tempArtist = new MPDArtist(artistName);
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ARTIST_SORT_NAME)) {
                if (null != tempArtist) {
                    artistList.add(tempArtist);
                }
                artistName = response.substring(MPDResponses.MPD_RESPONSE_ARTIST_SORT_NAME.length());
                tempArtist = new MPDArtist(artistName);
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ALBUMARTIST_SORT_NAME)) {
                if (null != tempArtist) {
                    artistList.add(tempArtist);
                }
                artistName = response.substring(MPDResponses.MPD_RESPONSE_ALBUMARTIST_SORT_NAME.length());
                tempArtist = new MPDArtist(artistName);
            }

            // Handle artist properties
            if (tempArtist != null) {
                if (response.startsWith(MPDResponses.MPD_RESPONSE_ARTIST_MBID)) {
                    artistMBID = response.substring(MPDResponses.MPD_RESPONSE_ARTIST_MBID.length());
                    tempArtist.addMBID(artistMBID);
                }
            }
            response = connection.readLine();
        }


        // Add last artist
        if (null != tempArtist) {
            artistList.add(tempArtist);
        }

        // Sort the artists for later sectioning.
        Collections.sort(artistList);

        // If we used MBID filtering, it could happen that a user has an artist in the list multiple times,
        // once with and once without MBID. Try to filter this by sorting the list first by name and mbid count
        // and then remove duplicates.
        if (hasMusicBrainz && hasListGroup) {
            ArrayList<MPDArtist> clearedList = new ArrayList<>();

            // Remove multiple entries when one artist is in list with and without MBID
            int artistListSize = artistList.size();
            for (int i = 0; i < artistListSize; i++) {
                MPDArtist artist = artistList.get(i);
                if (i + 1 != artistListSize) {
                    MPDArtist nextArtist = artistList.get(i + 1);
                    // Next artist is different, add this one (the one with most MBIDs)
                    if (!artist.getArtistName().equals(nextArtist.getArtistName())) {
                        clearedList.add(artist);
                    }
                } else {
                    // Last artist in list -> add
                    clearedList.add(artist);
                }
            }


            return clearedList;
        } else {
            return artistList;
        }
    }

    /**
     * Parses the response of mpd on requests that return track items. This is also used
     * for MPD file, directory and playlist responses. This allows the GUI to develop
     * one adapter for all three types. Also MPD mixes them when requesting directory listings.
     * <p/>
     * It will return a list of MPDFileEntry objects which is a parent class for (MPDTrack, MPDPlaylist,
     * MPDDirectory) you can use instanceof to check which type you got.
     *
     * @return List of MPDFileEntry objects
     * @throws MPDException if an error from MPD was received during reading
     */
    static ArrayList<MPDFileEntry> parseMPDTracks(final MPDConnection connection) throws MPDException {
        ArrayList<MPDFileEntry> trackList = new ArrayList<>();
        if (!connection.isConnected()) {
            return trackList;
        }

        /* Temporary file entry (added to list later) */
        MPDFileEntry tempFileEntry = null;

        /* Response line from MPD */
        String response = connection.readLine();
        while (response != null && !response.startsWith("OK")) {
            /* This if block will just check all the different response possible by MPDs file/dir/playlist response */
            if (response.startsWith(MPDResponses.MPD_RESPONSE_FILE)) {
                if (null != tempFileEntry) {
                    trackList.add(tempFileEntry);
                }
                tempFileEntry = new MPDTrack(response.substring(MPDResponses.MPD_RESPONSE_FILE.length()));
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_PLAYLIST)) {
                if (null != tempFileEntry) {
                    trackList.add(tempFileEntry);
                }
                tempFileEntry = new MPDPlaylist(response.substring(MPDResponses.MPD_RESPONSE_PLAYLIST.length()));
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_DIRECTORY)) {
                if (null != tempFileEntry) {
                    trackList.add(tempFileEntry);
                }
                tempFileEntry = new MPDDirectory(response.substring(MPDResponses.MPD_RESPONSE_DIRECTORY.length()));
            }

            // Currently parsing a file (check its properties)
            if (tempFileEntry instanceof MPDTrack) {
                if (response.startsWith(MPDResponses.MPD_RESPONSE_TRACK_TITLE)) {
                    ((MPDTrack) tempFileEntry).setTrackTitle(response.substring(MPDResponses.MPD_RESPONSE_TRACK_TITLE.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ARTIST_NAME)) {
                    ((MPDTrack) tempFileEntry).setTrackArtist(response.substring(MPDResponses.MPD_RESPONSE_ARTIST_NAME.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ARTIST_SORT_NAME)) {
                    ((MPDTrack) tempFileEntry).setTrackArtistSort(response.substring(MPDResponses.MPD_RESPONSE_ARTIST_SORT_NAME.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_TRACK_NAME)) {
                    ((MPDTrack) tempFileEntry).setTrackName(response.substring(MPDResponses.MPD_RESPONSE_TRACK_NAME.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ALBUMARTIST_NAME)) {
                    ((MPDTrack) tempFileEntry).setTrackAlbumArtist(response.substring(MPDResponses.MPD_RESPONSE_ALBUMARTIST_NAME.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ALBUMARTIST_SORT_NAME)) {
                    ((MPDTrack) tempFileEntry).setTrackAlbumArtistSort(response.substring(MPDResponses.MPD_RESPONSE_ALBUMARTIST_SORT_NAME.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ALBUM_NAME)) {
                    ((MPDTrack) tempFileEntry).setTrackAlbum(response.substring(MPDResponses.MPD_RESPONSE_ALBUM_NAME.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_DATE)) {
                    ((MPDTrack) tempFileEntry).setDate(response.substring(MPDResponses.MPD_RESPONSE_DATE.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ALBUM_MBID)) {
                    ((MPDTrack) tempFileEntry).setTrackAlbumMBID(response.substring(MPDResponses.MPD_RESPONSE_ALBUM_MBID.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ARTIST_MBID)) {
                    ((MPDTrack) tempFileEntry).setTrackArtistMBID(response.substring(MPDResponses.MPD_RESPONSE_ARTIST_MBID.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ALBUM_ARTIST_MBID)) {
                    ((MPDTrack) tempFileEntry).setTrackAlbumArtistMBID(response.substring(MPDResponses.MPD_RESPONSE_ALBUM_ARTIST_MBID.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_TRACK_MBID)) {
                    ((MPDTrack) tempFileEntry).setTrackMBID(response.substring(MPDResponses.MPD_RESPONSE_TRACK_MBID.length()));
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_TRACK_TIME)) {
                    try {
                        ((MPDTrack) tempFileEntry).setLength(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_TRACK_TIME.length())));
                    } catch (NumberFormatException ignored) {
                    }
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_SONG_ID)) {
                    try {
                        ((MPDTrack) tempFileEntry).setSongID(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_SONG_ID.length())));
                    } catch (NumberFormatException ignored) {
                    }
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_SONG_POS)) {
                    try {
                        ((MPDTrack) tempFileEntry).setSongPosition(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_SONG_POS.length())));
                    } catch (NumberFormatException ignored) {
                    }
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_DISC_NUMBER)) {
                /*
                * Check if MPD returned a discnumber like: "1" or "1/3" and set disc count accordingly.
                */
                    String discNumber = response.substring(MPDResponses.MPD_RESPONSE_DISC_NUMBER.length());
                    discNumber = discNumber.replaceAll(" ", "");
                    String[] discNumberSep = discNumber.split("/");
                    if (discNumberSep.length > 0) {
                        try {
                            ((MPDTrack) tempFileEntry).setDiscNumber(Integer.valueOf(discNumberSep[0]));
                        } catch (NumberFormatException ignored) {
                        }

                        if (discNumberSep.length > 1) {
                            try {
                                ((MPDTrack) tempFileEntry).psetAlbumDiscCount(Integer.valueOf(discNumberSep[1]));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    } else {
                        try {
                            ((MPDTrack) tempFileEntry).setDiscNumber(Integer.valueOf(discNumber));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_TRACK_NUMBER)) {
                /*
                 * Check if MPD returned a tracknumber like: "12" or "12/42" and set albumtrack count accordingly.
                 */
                    String trackNumber = response.substring(MPDResponses.MPD_RESPONSE_TRACK_NUMBER.length());
                    trackNumber = trackNumber.replaceAll(" ", "");
                    String[] trackNumbersSep = trackNumber.split("/");
                    if (trackNumbersSep.length > 0) {
                        try {
                            ((MPDTrack) tempFileEntry).setTrackNumber(Integer.valueOf(trackNumbersSep[0]));
                        } catch (NumberFormatException ignored) {
                        }
                        if (trackNumbersSep.length > 1) {
                            try {
                                ((MPDTrack) tempFileEntry).setAlbumTrackCount(Integer.valueOf(trackNumbersSep[1]));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    } else {
                        try {
                            ((MPDTrack) tempFileEntry).setTrackNumber(Integer.valueOf(trackNumber));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                } else if (response.startsWith(MPDResponses.MPD_RESPONSE_LAST_MODIFIED)) {
                    tempFileEntry.setLastModified(response.substring(MPDResponses.MPD_RESPONSE_LAST_MODIFIED.length()));
                }
            } else if (tempFileEntry != null) {
                // Other case tempFileEntry is a playlist or a directory (properties of generic files)
                if (response.startsWith(MPDResponses.MPD_RESPONSE_LAST_MODIFIED)) {
                    tempFileEntry.setLastModified(response.substring(MPDResponses.MPD_RESPONSE_LAST_MODIFIED.length()));
                }
            }

            // Move to the next line.
            response = connection.readLine();
        }

        /* Add last remaining track to list. */
        if (null != tempFileEntry) {
            trackList.add(tempFileEntry);
        }
        return trackList;
    }

    static MPDCurrentStatus parseMPDCurrentStatus(final MPDConnection connection) throws MPDException {
        MPDCurrentStatus status = new MPDCurrentStatus();
        if (!connection.isConnected()) {
            return status;
        }

        /* Response line from MPD */
        String response = connection.readLine();
        while (response != null && !response.startsWith("OK")) {
            if (response.startsWith(MPDResponses.MPD_RESPONSE_VOLUME)) {
                try {
                    status.setVolume(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_VOLUME.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_REPEAT)) {
                try {
                    status.setRepeat(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_REPEAT.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_RANDOM)) {
                try {
                    status.setRandom(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_RANDOM.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_SINGLE)) {
                try {
                    status.setSinglePlayback(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_SINGLE.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_CONSUME)) {
                try {
                    status.setConsume(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_CONSUME.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_PLAYLIST_VERSION)) {
                try {
                    status.setPlaylistVersion(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_PLAYLIST_VERSION.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_PLAYLIST_LENGTH)) {
                try {
                    status.setPlaylistLength(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_PLAYLIST_LENGTH.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_PLAYBACK_STATE)) {
                String state = response.substring(MPDResponses.MPD_RESPONSE_PLAYBACK_STATE.length());

                switch (state) {
                    case MPDResponses.MPD_PLAYBACK_STATE_RESPONSE_PLAY:
                        status.setPlaybackState(MPDCurrentStatus.MPD_PLAYBACK_STATE.MPD_PLAYING);
                        break;
                    case MPDResponses.MPD_PLAYBACK_STATE_RESPONSE_PAUSE:
                        status.setPlaybackState(MPDCurrentStatus.MPD_PLAYBACK_STATE.MPD_PAUSING);
                        break;
                    case MPDResponses.MPD_PLAYBACK_STATE_RESPONSE_STOP:
                        status.setPlaybackState(MPDCurrentStatus.MPD_PLAYBACK_STATE.MPD_STOPPED);
                        break;
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_CURRENT_SONG_INDEX)) {
                try {
                    status.setCurrentSongIndex(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_CURRENT_SONG_INDEX.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_NEXT_SONG_INDEX)) {
                try {
                    status.setNextSongIndex(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_NEXT_SONG_INDEX.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_TIME_INFORMATION_OLD)) {
                String timeInfo = response.substring(MPDResponses.MPD_RESPONSE_TIME_INFORMATION_OLD.length());

                String timeInfoSep[] = timeInfo.split(":");
                if (timeInfoSep.length == 2) {
                    try {
                        status.setElapsedTime(Integer.valueOf(timeInfoSep[0]));
                        status.setTrackLength(Integer.valueOf(timeInfoSep[1]));
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_ELAPSED_TIME)) {
                try {
                    status.setElapsedTime(Math.round(Float.valueOf(response.substring(MPDResponses.MPD_RESPONSE_ELAPSED_TIME.length()))));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_DURATION)) {
                try {
                    status.setTrackLength(Math.round(Float.valueOf(response.substring(MPDResponses.MPD_RESPONSE_DURATION.length()))));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_BITRATE)) {
                try {
                    status.setBitrate(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_BITRATE.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_AUDIO_INFORMATION)) {
                String audioInfo = response.substring(MPDResponses.MPD_RESPONSE_AUDIO_INFORMATION.length());

                String audioInfoSep[] = audioInfo.split(":");
                if (audioInfoSep.length == 3) {
                /* Extract the separate pieces */
                    try {
                /* First is sampleRate */
                        status.setSamplerate(Integer.valueOf(audioInfoSep[0]));
                /* Second is bitresolution */
                        status.setBitDepth(audioInfoSep[1]);
                /* Third is channel count */
                        status.setChannelCount(Integer.valueOf(audioInfoSep[2]));
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else if (response.startsWith(MPDResponses.MPD_RESPONSE_UPDATING_DB)) {
                try {
                    status.setUpdateDBJob(Integer.valueOf(response.substring(MPDResponses.MPD_RESPONSE_UPDATING_DB.length())));
                } catch (NumberFormatException ignored) {
                }
            }

            response = connection.readLine();
        }
        return status;
    }

    /**
     * Parses the MPD response to a statistics request
     *
     * @param connection {@link MPDConnection} to use
     * @return Statistic object just parsed
     * @throws MPDException Thrown if MPD throws an error
     */
    static MPDStatistics parseMPDStatistic(final MPDConnection connection) throws MPDException {
        MPDStatistics stats = new MPDStatistics();

        /* Response line from MPD */
        String response = connection.readLine();
        while (response != null && !response.startsWith("OK")) {
            if (response.startsWith(MPDResponses.MPD_STATS_UPTIME)) {
                try {
                    stats.setServerUptime(Integer.valueOf(response.substring(MPDResponses.MPD_STATS_UPTIME.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_STATS_PLAYTIME)) {
                try {
                    stats.setPlayDuration(Integer.valueOf(response.substring(MPDResponses.MPD_STATS_PLAYTIME.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_STATS_ARTISTS)) {
                try {
                    stats.setArtistsCount(Integer.valueOf(response.substring(MPDResponses.MPD_STATS_ARTISTS.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_STATS_ALBUMS)) {
                try {
                    stats.setAlbumCount(Integer.valueOf(response.substring(MPDResponses.MPD_STATS_ALBUMS.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_STATS_SONGS)) {
                try {
                    stats.setSongCount(Integer.valueOf(response.substring(MPDResponses.MPD_STATS_SONGS.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_STATS_DB_PLAYTIME)) {
                try {
                    stats.setAllSongDuration(Integer.valueOf(response.substring(MPDResponses.MPD_STATS_DB_PLAYTIME.length())));
                } catch (NumberFormatException ignored) {
                }
            } else if (response.startsWith(MPDResponses.MPD_STATS_DB_LAST_UPDATE)) {
                try {
                    stats.setLastDBUpdate(Long.valueOf(response.substring(MPDResponses.MPD_STATS_DB_LAST_UPDATE.length())));
                } catch (NumberFormatException ignored) {
                }
            }

            response = connection.readLine();
        }
        return stats;
    }

    /**
     * Private parsing method for MPDs command list
     *
     * @return A list of Strings of commands that are allowed on the server
     * @throws IOException If an IO error occurs during read
     * @throws MPDException if an error from MPD was received during reading
     */
    static List<String> parseMPDCommands(final MPDConnection connection) throws IOException, MPDException {
        ArrayList<String> commandList = new ArrayList<>();
        // Parse outputs
        String commandName;
        /*if (!connection.isConnected()) {
            return commandList;
        }*/

        /* Response line from MPD */
        String response = connection.readLine();
        while (response != null && !response.startsWith("OK")) {
            if (response.startsWith(MPDResponses.MPD_COMMAND)) {
                commandName = response.substring(MPDResponses.MPD_COMMAND.length());
                commandList.add(commandName);
            }
            response = connection.readLine();
        }
        return commandList;

    }

    /**
     * Parses the response of MPDs supported tag types
     *
     * @return List of tags supported by the connected MPD host
     * @throws IOException If an IO error occurs during read
     * @throws MPDException if an error from MPD was received during reading
     */
    static List<String> parseMPDTagTypes(final MPDConnection connection) throws IOException, MPDException {
        ArrayList<String> tagList = new ArrayList<>();
        // Parse outputs
        String tagName;
        /*if (!connection.isConnected()) {
            return tagList;
        }*/

        /* Response line from MPD */
        String response = connection.readLine();
        while (response != null && !response.startsWith("OK") && !response.startsWith("ACK")) {
            if (response.startsWith(MPDResponses.MPD_TAGTYPE)) {
                tagName = response.substring(MPDResponses.MPD_TAGTYPE.length());
                tagList.add(tagName);
            }
            response = connection.readLine();
        }

        return tagList;
    }

    /**
     * Private parsing method for MPDs output lists.
     *
     * @return A list of MPDOutput objects with name,active,id values if successful. Otherwise empty list.
     * @throws MPDException if an error from MPD was received during reading
     */
    static List<MPDOutput> parseMPDOutputs(final MPDConnection connection) throws MPDException {
        ArrayList<MPDOutput> outputList = new ArrayList<>();
        // Parse outputs
        String outputName = null;
        boolean outputActive = false;
        int outputId = -1;

        if (!connection.isConnected()) {
            return null;
        }

        /* Response line from MPD */
        String response = connection.readLine();
        while (response != null && !response.startsWith("OK")) {
            if (response.startsWith(MPDResponses.MPD_OUTPUT_ID)) {
                if (null != outputName) {
                    MPDOutput tempOutput = new MPDOutput(outputName, outputActive, outputId);
                    outputList.add(tempOutput);
                }
                outputId = Integer.valueOf(response.substring(MPDResponses.MPD_OUTPUT_ID.length()));
            } else if (response.startsWith(MPDResponses.MPD_OUTPUT_NAME)) {
                outputName = response.substring(MPDResponses.MPD_OUTPUT_NAME.length());
            } else if (response.startsWith(MPDResponses.MPD_OUTPUT_ACTIVE)) {
                String activeRespsonse = response.substring(MPDResponses.MPD_OUTPUT_ACTIVE.length());
                outputActive = activeRespsonse.equals("1");
            }
            response = connection.readLine();
        }

        // Add remaining output to list
        if (null != outputName) {
            MPDOutput tempOutput = new MPDOutput(outputName, outputActive, outputId);
            outputList.add(tempOutput);
        }

        return outputList;
    }
}
