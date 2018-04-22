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


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class MPDArtist implements MPDGenericItem, Comparable<MPDArtist>, Parcelable {
    /* Artist properties */
    @NonNull
    private String pArtistName;

    /* Musicbrainz ID */
    @NonNull
    private ArrayList<String> pMBIDs;

    private boolean mImageFetching;

    public MPDArtist(@NonNull String name) {
        pArtistName = name;
        pMBIDs = new ArrayList<>();
    }

    protected MPDArtist(Parcel in) {
        pArtistName = in.readString();
        pMBIDs = in.createStringArrayList();
        mImageFetching = in.readByte() != 0;
    }

    public static final Creator<MPDArtist> CREATOR = new Creator<MPDArtist>() {
        @Override
        public MPDArtist createFromParcel(Parcel in) {
            return new MPDArtist(in);
        }

        @Override
        public MPDArtist[] newArray(int size) {
            return new MPDArtist[size];
        }
    };

    @NonNull
    public String getArtistName() {
        return pArtistName;
    }

    public int getMBIDCount() {
        return pMBIDs.size();
    }

    public String getMBID(int position) {
        return pMBIDs.get(position);
    }

    public void addMBID(@NonNull String mbid) {
        pMBIDs.add(mbid);
    }

    public void setMBID(@NonNull String mbid) {
        pMBIDs.clear();
        pMBIDs.add(mbid);
    }


    @Override
    @NonNull
    public String getSectionTitle() {
        return pArtistName;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MPDArtist)) {
            return false;
        }

        MPDArtist artist = (MPDArtist) object;
        if (!artist.pArtistName.toLowerCase().equals(pArtistName.toLowerCase()) || (artist.pMBIDs.size() != pMBIDs.size())) {
            return false;
        }

        for (int i = 0; i < pMBIDs.size(); i++) {
            if (!pMBIDs.get(i).equals(artist.pMBIDs.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(@NonNull MPDArtist another) {
        if (another.equals(this)) {
            return 0;
        }

        if (another.pArtistName.toLowerCase().equals(pArtistName.toLowerCase())) {
            //Log.v(MPDArtist.class.getSimpleName(),"another mbids: " + another.pMBIDs.size() + "self mbids:" + pMBIDs.size());
            // Try to position artists with one mbid at the end

            int size = pMBIDs.size();
            int anotherSize = another.pMBIDs.size();
            if (size > anotherSize) {
                // This object is "greater" than another
                return 1;
            } else if (size < anotherSize) {
                // This object is "less" than another
                return -1;
            } else {
                // Create some random order for MBID arrays (must be consistent)
                if (size > 0) {
                    return pMBIDs.get(0).compareTo(another.pMBIDs.get(0));
                } else {
                    return 0;
                }
            }
        }

        return pArtistName.toLowerCase().compareTo(another.pArtistName.toLowerCase());
    }

    public synchronized void setFetching(boolean fetching) {
        mImageFetching = fetching;
    }

    public synchronized boolean getFetching() {
        return mImageFetching;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder(this.pArtistName + "_");
        for (String mbid : pMBIDs) {
            retVal.append("_").append(mbid);
        }
        return retVal.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pArtistName);
        String[] mbids = pMBIDs.toArray(new String[pMBIDs.size()]);
        dest.writeStringArray(mbids);
        dest.writeByte(mImageFetching ? (byte) 1 : (byte) 0);
    }
}
