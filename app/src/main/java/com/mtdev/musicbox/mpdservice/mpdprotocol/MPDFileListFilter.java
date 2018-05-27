

package com.mtdev.musicbox.mpdservice.mpdprotocol;


import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.util.Iterator;
import java.util.List;

public class MPDFileListFilter {

    public static void filterAlbumArtistTracks(List<MPDFileEntry> list, String albumArtist) {
        filterMPDTrack(list, track -> albumArtist.toLowerCase().equals(track.getTrackAlbumArtist().toLowerCase())
                || albumArtist.toLowerCase().equals(track.getTrackArtist().toLowerCase()));
    }

    public static void filterAlbumMBID(List<MPDFileEntry> list, String albumMBID) {
        filterMPDTrack(list, track -> albumMBID.toLowerCase().equals(track.getTrackAlbumMBID().toLowerCase()));
    }

    public static void filterAlbumMBIDandAlbumArtist(List<MPDFileEntry> list, String albumMBID, String albumArtist) {
        filterMPDTrack(list, track ->
                // Check if MBID matches (or is empty in both cases (tag missing))
                (albumMBID.toLowerCase().equals(track.getTrackAlbumMBID().toLowerCase()))
                        // Check if artist tag matches
                        && ((!albumArtist.isEmpty() && !track.getTrackArtist().isEmpty() && track.getTrackArtist().toLowerCase().equals(albumArtist.toLowerCase()))
                        ||
                        // OR if albumartist tag matches
                        (!albumArtist.isEmpty() && !track.getTrackAlbumArtist().isEmpty() && track.getTrackAlbumArtist().toLowerCase().equals(albumArtist.toLowerCase())))
        );
    }

    public static void filterAlbumMBIDandAlbumArtistSort(List<MPDFileEntry> list, String albumMBID, String albumArtist) {
        filterMPDTrack(list, track ->
                // Check if MBID matches (or is empty in both cases (tag missing))
                (albumMBID.toLowerCase().equals(track.getTrackAlbumMBID().toLowerCase()))
                        // Check if artist tag matches
                        && ((!albumArtist.isEmpty() && !track.getTrackArtistSort().isEmpty() && track.getTrackArtistSort().toLowerCase().equals(albumArtist.toLowerCase()))
                        ||
                        // OR if albumartist tag matches
                        (!albumArtist.isEmpty() && !track.getTrackAlbumArtistSort().isEmpty() && track.getTrackAlbumArtistSort().toLowerCase().equals(albumArtist.toLowerCase())))
        );
    }


    private static void filterMPDTrack(List<MPDFileEntry> list, MPDFileFilter filter) {
        Iterator<MPDFileEntry> iterator = list.iterator();

        while (iterator.hasNext()) {
            MPDFileEntry item = iterator.next();
            if (item instanceof MPDTrack) {
                if (!filter.accept((MPDTrack) item)) {
                    // If filter does not match, remove element
                    iterator.remove();
                }
            }
        }
    }

    private interface MPDFileFilter {
        boolean accept(MPDTrack track);
    }
}
