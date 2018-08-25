package com.example.android.inventory_app_udacity_project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.inventory_app_udacity_project.data.BookContract.BookEntry.*;

/**
 * Created by Ken Muckey on 8/1/2018.
 */
public class BookDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;
    private static final String COMMA_SEP = ",";
    private static final String TEXT_TYPE = "TEXT";
    private static final String INT_TYPE = "INT";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS" + " " + TABLE_NAME;

    // Identify Logging Source
    private static final String LOG_TAG = BookDBHelper.class.getName();

    //constructor
    public BookDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Overriding onCreate to create and run CREATE SQL command
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " " + TEXT_TYPE + " NOT NULL " + COMMA_SEP +
                        COLUMN_SUMMARY + " " + TEXT_TYPE + COMMA_SEP +
                        COLUMN_PRICE + " " + INT_TYPE + " NOT NULL DEFAULT 0 " + COMMA_SEP +
                        COLUMN_QUANTITY + " " + INT_TYPE + " NOT NULL DEFAULT 0 " + COMMA_SEP +
                        COLUMN_SUPPLIER_NAME + " " + TEXT_TYPE + " NOT NULL " + COMMA_SEP +
                        COLUMN_SUPPLIER_PHONE + " " + INT_TYPE + " NOT NULL DEFAULT 0 " +
                        " )";
        Log.i(LOG_TAG, "Table create SQL commeand compiled: " + SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * Upon Upgrade, deletes entries and runs onCreate again
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
