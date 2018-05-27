

package com.mtdev.musicbox.mpdservice.mpdprotocol;

import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.util.Collections;
import java.util.List;

public class MPDSortHelper {

    /**
     * Sorts a list of {@link MPDFileEntry} objects in the right order of their index (if
     * the objects are from type {@link MPDTrack}. All other elements are located at the end of the list.
     * @param inList List of objects to sort.
     */
    public static void sortFileListNumeric(List<MPDFileEntry> inList) {
        Collections.sort(inList, new MPDFileEntry.MPDFileIndexComparator());
    }
}
