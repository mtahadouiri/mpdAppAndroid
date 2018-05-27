
package com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

public class MPDAlbum implements MPDGenericItem, Comparable<MPDAlbum>, Parcelable {
    public enum MPD_ALBUM_SORT_ORDER {
        TITLE, // Default value
        DATE
    }

    /* Album properties */
    @NonNull
    private String mName;

    /* Musicbrainz ID */
    @NonNull
    private String mMBID;

    /* Artists name (if any) */
    @NonNull
    private String mArtistName;

    @NonNull
    private String mArtistSortName;

    @NonNull
    private Date mDate;

    private boolean mImageFetching;

    public MPDAlbum(@NonNull String name) {
        mName = name;
        mMBID = "";
        mArtistName = "";
        mArtistSortName = "";
        mDate = new Date(0);
    }

    /* Getters */

    protected MPDAlbum(Parcel in) {
        mName = in.readString();
        mMBID = in.readString();
        mArtistName = in.readString();
        mArtistSortName = in.readString();
        mImageFetching = in.readByte() != 0;
        mDate = (Date) in.readSerializable();
    }

    public static final Creator<MPDAlbum> CREATOR = new Creator<MPDAlbum>() {
        @Override
        public MPDAlbum createFromParcel(Parcel in) {
            return new MPDAlbum(in);
        }

        @Override
        public MPDAlbum[] newArray(int size) {
            return new MPDAlbum[size];
        }
    };

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getMBID() {
        return mMBID;
    }

    @NonNull
    public String getArtistName() {
        return mArtistName;
    }


    public void setArtistName(@NonNull String artistName) {
        mArtistName = artistName;
    }

    @NonNull
    public String getArtistSortName() {
        return mArtistSortName;
    }

    public void setArtistSortName(@NonNull String artistSortName) {
        mArtistSortName = artistSortName;
    }

    public void setMBID(@NonNull String mbid) {
        mMBID = mbid;
    }

    public void setDate(@NonNull Date date) {
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    @Override
    @NonNull
    public String getSectionTitle() {
        return mName;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MPDAlbum)) {
            return false;
        }

        MPDAlbum album = (MPDAlbum) object;
        return (mName.equals(album.mName)) && (mArtistName.equals(album.mArtistName)) &&
                (mMBID.equals(album.mMBID)) && (mDate.equals(album.mDate));
    }

    @Override
    public int compareTo(@NonNull MPDAlbum another) {
        if (another.equals(this)) {
            return 0;
        }
        return mName.toLowerCase().compareTo(another.mName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return (mName + mArtistName + mMBID).hashCode();
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
        return mName + "_" + mArtistName + "_" + mMBID + "_" + mDate;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mMBID);
        dest.writeString(mArtistName);
        dest.writeString(mArtistSortName);
        dest.writeByte((byte) (mImageFetching ? 1 : 0));
        dest.writeSerializable(mDate);
    }

    public static class MPDAlbumDateComparator implements Comparator<MPDAlbum> {

        @Override
        public int compare(MPDAlbum o1, MPDAlbum o2) {
            if (o2.equals(o1)) {
                return 0;
            }
            return o1.mDate.compareTo(o2.mDate);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MPDAlbum && obj.equals(this);
        }
    }
}
