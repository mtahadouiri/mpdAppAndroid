

package com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects;


import android.support.annotation.NonNull;

public class MPDPlaylist extends MPDFileEntry implements MPDGenericItem {
    public MPDPlaylist(@NonNull String path) {
        super(path);
    }

    @Override
    public String getSectionTitle() {
        return getFilename();
    }

    public int compareTo(@NonNull MPDPlaylist another) {
        String title = getFilename();
        String anotherTitle = another.getFilename();
        return title.toLowerCase().compareTo(anotherTitle.toLowerCase());
    }
}
