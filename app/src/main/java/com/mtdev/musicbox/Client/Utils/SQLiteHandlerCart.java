package com.mtdev.musicbox.Client.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mtdev.musicbox.Client.Entities.Product;

import java.util.ArrayList;

/**
 * Created by Taha on 22/04/2018.
 */

public class SQLiteHandlerCart extends SQLiteOpenHelper {

    public static int paymentPrix= 0;
    private static final String TAG = SQLiteHandlerCart.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MusicBoxAndroid";

    // Login table name
    private static final String TABLE = "cart";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMG = "img";
    private static final String KEY_price = "price";
    private static final String KEY_quantity = "quantity";

    public SQLiteHandlerCart(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IMG + " TEXT ,"  + KEY_price + " INTEGER," +
                KEY_quantity + " INTEGER" +")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addProduct(String name, String img,int price,int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // name
        values.put(KEY_IMG, img); // img
        values.put(KEY_price, price); // p
        values.put(KEY_quantity, quantity); // q

        // Inserting Row
        long id = db.insert(TABLE, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New product inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public ArrayList<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        Log.d("Cursor",cursor.getCount()+"");
        while (cursor.moveToNext()) {
            Product p = new Product();
            p.setName(cursor.getString(1));
            p.setImgUrl(cursor.getString(2));
            p.setPrice(cursor.getInt(3));
            p.setQuantity(cursor.getInt(4));
            paymentPrix += cursor.getInt(3);
            products.add(p);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + products.toString());
        return products;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}