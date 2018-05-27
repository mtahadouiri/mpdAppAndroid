

package com.mtdev.musicbox.application.artworkdatabase;

import android.database.sqlite.SQLiteDatabase;

public class ArtistArtTable {
    public static final String TABLE_NAME = "malp_artist_artwork_items";

    public static final String COLUMN_ARTIST_NAME = "artist_name";

    public static final String COLUMN_ARTIST_MBID = "artist_mbid";

    public static final String COLUMN_IMAGE_FILE_PATH = "artist_image_file_path";

    public static final String COLUMN_IMAGE_NOT_FOUND = "image_not_found";

    private static final String DATABASE_CREATE = "CREATE TABLE if not exists " +
            TABLE_NAME +
            " (" +
            COLUMN_ARTIST_NAME + " text," +
            COLUMN_ARTIST_MBID + " text," +
            COLUMN_IMAGE_NOT_FOUND + " integer," +
            COLUMN_IMAGE_FILE_PATH + " text" +
            ");";

    private static final String DATABASE_DROP = "DROP TABLE if exists " + TABLE_NAME;

    public static void createTable(SQLiteDatabase database) {
        // Create table if not already existing
        database.execSQL(DATABASE_CREATE);
    }

    public static void dropTable(final SQLiteDatabase database) {
        // drop table if already existing
        database.execSQL(DATABASE_DROP);
    }
}
