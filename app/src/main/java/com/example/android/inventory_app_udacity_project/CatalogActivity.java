package com.example.android.inventory_app_udacity_project;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventory_app_udacity_project.data.BookContract.BookEntry;
import com.example.android.inventory_app_udacity_project.data.BookCursorAdapter;

/**
 * Main activity of the app, displaying data.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Global Variables
    public static final String LOG_TAG = CatalogActivity.class.getName();
    // 0 value is arbitrary as there is the only loader in this class
    private static final int URL_LOADER = 0;
    // The adapter used to display each list item entry in list view
    BookCursorAdapter myAdapter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[]{
            BookEntry._ID,
            BookEntry.COLUMN_NAME,
            BookEntry.COLUMN_SUMMARY,
            BookEntry.COLUMN_PRICE,
            BookEntry.COLUMN_QUANTITY,
            BookEntry.COLUMN_SUPPLIER_NAME,
            BookEntry.COLUMN_SUPPLIER_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity when the button is clicked
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find ListView to populate
        ListView myListView = (ListView) findViewById(R.id.catalogactivity_text_view);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        myListView.setEmptyView(emptyView);


        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        myAdapter = new BookCursorAdapter(this, null);
        myListView.setAdapter(myAdapter);

        // Create a listener for when an entry is clicked
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Create an URI for specific item that was clicked and starts activity
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        // Init the Cursor Loader
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    /*
     *  Inserts a sample data row into the table
     *  Updates Texview on screen with incremented row count
     */
    private void insertItem() {

        // Create new map of values to input into the table
        ContentValues itemValues = new ContentValues();
        itemValues.put(BookEntry.COLUMN_NAME, getString(R.string.sample_name));
        // Sample summary sourced from Wikipedia: https://en.wikipedia.org/wiki/The_Player_of_Games
        itemValues.put(BookEntry.COLUMN_SUMMARY, getString(R.string.sample_summary));
        itemValues.put(BookEntry.COLUMN_PRICE, 19.88);
        itemValues.put(BookEntry.COLUMN_QUANTITY, 7);
        itemValues.put(BookEntry.COLUMN_SUPPLIER_NAME, getString(R.string.sample_supplier));
        itemValues.put(BookEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.sample_supplier_phone));

        Log.i(LOG_TAG,"INSERTITEM item Values: " + itemValues);
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, itemValues);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_action_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.action_insert_saved),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, BookEntry.CONTENT_URI,
                PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        myAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        myAdapter.swapCursor(null);
    }

    /**
     * Helper method to delete all entries in the database.
     */
    private void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + getString(R.string.rows_deleted_message));
    }
}
