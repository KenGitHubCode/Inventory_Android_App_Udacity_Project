package com.example.android.inventory_app_udacity_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventory_app_udacity_project.data.BookContract.BookEntry;
import com.example.android.inventory_app_udacity_project.data.BookDBHelper;

/**
 * Main activity of the app, displaying data
 */
public class CatalogActivity extends AppCompatActivity {
    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    BookDBHelper mDbHelper = new BookDBHelper(this);
    // Log variable to identify source of log entry when debugging
    public static final String LOG_TAG = CatalogActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Create instance of the DB Helper class
        BookDBHelper mDbHelper = new BookDBHelper(this);

        // Create instance of the SQLiteDatabase class
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        displayDatabaseInfo();
    }

    // Upon return to this activity, refresh data on screen
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                BookEntry.COLUMN_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                BookEntry._ID + " DESC";

        // Create a Cursor to navigate the data table
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        // Assign where to display the data
        TextView displayView = (TextView) findViewById(R.id.text_view_book);

        try {
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The " + BookEntry.TABLE_NAME + " table contains " + cursor.getCount() + " Books.\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_NAME + " - " +
                    BookEntry.COLUMN_PRICE + " - " +
                    BookEntry.COLUMN_QUANTITY + " - " +
                    BookEntry.COLUMN_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_SUPPLIER_PHONE + "\n"
            );

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                Double currentPrice = cursor.getDouble(priceColumnIndex);
                int currentquantity = cursor.getInt(quantityColumnIndex);
                String currentsupplier = cursor.getString(supplierNameColumnIndex);
                int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentquantity + " - " +
                        currentsupplier + " - " +
                        supplierPhone
                ));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    /*
     *  Inserts a data row into the table
     *  Updates Texview on screen with incremented row count
     */
    private void insertBook() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create new map of values to input into the table
        ContentValues bookValues = new ContentValues();
        bookValues.put(BookEntry.COLUMN_NAME, "The Player of Games");
        bookValues.put(BookEntry.COLUMN_PRICE, "12.44");
        bookValues.put(BookEntry.COLUMN_QUANTITY, 4);
        bookValues.put(BookEntry.COLUMN_SUPPLIER_NAME, "Toshiba Publishing");
        bookValues.put(BookEntry.COLUMN_SUPPLIER_PHONE, 1234567890);

        // Check if valid row returned (negative 1 is invalid)
        Long newRowID = db.insert(BookEntry.TABLE_NAME, null, bookValues);
        Log.i(LOG_TAG, "bookValues: " + bookValues);
        if (newRowID == -1) {
            Log.e(LOG_TAG, "Insert failed");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                // Updates Texview on screen with incremented row count
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

