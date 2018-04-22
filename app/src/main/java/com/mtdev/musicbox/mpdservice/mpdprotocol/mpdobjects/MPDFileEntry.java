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

package com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class MPDFileEntry implements MPDGenericItem, Comparable<MPDFileEntry> {
    @NonNull
    String mPath = "";

    @NonNull
    private Date mLastModifiedDate = new Date(0);

    protected MPDFileEntry() {}

    protected MPDFileEntry(@NonNull String path) {
        mPath = path;
    }

    public void setPath(@NonNull String path) {
        mPath = path;
    }

    @NonNull
    public String getPath() {
        return mPath;
    }

    @NonNull
    public String getFilename() {
        return mPath.substring(mPath.lastIndexOf('/') + 1);
    }

    public void setLastModified(@NonNull String lastModified) {
        // Try to parse date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ROOT);
        // Assume MPD sends time as UTC time
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            mLastModifiedDate = format.parse(lastModified);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    public String getLastModifiedString() {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
        return format.format(mLastModifiedDate);
    }

    /**
     * This methods defines an hard order of directory, files, playlists
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(@NonNull MPDFileEntry another) {
        if (this instanceof MPDDirectory) {
            if (another instanceof MPDDirectory) {
                return ((MPDDirectory) this).compareTo((MPDDirectory) another);
            } else if (another instanceof MPDPlaylist || another instanceof MPDTrack) {
                return -1;
            }
        } else if (this instanceof MPDTrack) {
            if (another instanceof MPDDirectory) {
                return 1;
            } else if (another instanceof MPDPlaylist) {
                return -1;
            } else if (another instanceof MPDTrack) {
                return ((MPDTrack) this).compareTo((MPDTrack) another);
            }
        } else if (this instanceof MPDPlaylist) {
            if (another instanceof MPDPlaylist) {
                return ((MPDPlaylist) this).compareTo((MPDPlaylist) another);
            } else if (another instanceof MPDDirectory || another instanceof MPDTrack) {
                return 1;
            }
        }

        return -1;
    }

    public static class MPDFileIndexComparator implements Comparator<MPDFileEntry> {

        @Override
        public int compare(MPDFileEntry o1, MPDFileEntry o2) {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            }

            if (o1 instanceof MPDDirectory) {
                if (o2 instanceof MPDDirectory) {
                    return ((MPDDirectory) o1).compareTo((MPDDirectory) o2);
                } else if (o2 instanceof MPDPlaylist || o2 instanceof MPDTrack) {
                    return -1;
                }
            } else if (o1 instanceof MPDTrack) {
                if (o2 instanceof MPDDirectory) {
                    return 1;
                } else if (o2 instanceof MPDPlaylist) {
                    return -1;
                } else if (o2 instanceof MPDTrack) {
                    return ((MPDTrack) o1).indexCompare((MPDTrack) o2);
                }
            } else if (o1 instanceof MPDPlaylist) {
                if (o2 instanceof MPDPlaylist) {
                    return ((MPDPlaylist) o1).compareTo((MPDPlaylist) o2);
                } else if (o2 instanceof MPDDirectory || o2 instanceof MPDTrack) {
                    return 1;
                }
            }

            return -1;
        }
    }

}
