package com.example.android.inventory_app_udacity_project.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.inventory_app_udacity_project.data.BookContract.BookEntry;
import static com.example.android.inventory_app_udacity_project.data.BookContract.CONTENT_AUTHORITY;
import static com.example.android.inventory_app_udacity_project.data.BookContract.PATH_BOOKS;


/**
 * Created by Ken Muckey on 8/21/2018.
 */

/**
 * {@link ContentProvider} for Inventory app.
 */
public class BookProvider extends ContentProvider {

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    BookDBHelper mDbHelper;
    // Tag for the log messages
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    // URI matcher code for the content URI for the books table
    private static final int BOOKS = 100;

    // URI matcher code for the content URI for a single book in the books table
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookDBHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                // This will perform a query on the books table where and return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                Log.i(LOG_TAG, "Query is " + cursor);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.books/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID.
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table and to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification for cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     *
     * @param uri
     * @param contentValues
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     *
     * @param uri
     * @param values
     * @return
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Note: Input validation not needed for Summary. Any input, including null, is accepted.
        // Check that the name is not null
        String name = values.getAsString(BookContract.BookEntry.COLUMN_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Book requires a name");
        }

        // Checks quantity for: NULL and if valid input
        Integer myQuantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
        if (myQuantity.equals(null) || myQuantity < 0) {
            throw new IllegalArgumentException("Quantity Required.");
        }

        // Checks price for : NULL value is allowed (but the database will use a
        //  default of 0 if a value isn't provided). Also, must be a positive value.
        Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_PRICE);
        if (price.equals(null) || price < 0) {
            throw new IllegalArgumentException("Price but be a positive number.");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } else {
            Log.i(LOG_TAG, "Book has been inserted from BookProvider uri insertBook method with uri: " + uri);
        }

        // Notificaiton for URI if changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     *
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        Log.i(LOG_TAG, "Selection at start of updateBook" + selection);

        /**
         *  Input Validation / Sanity Check section
         */
        //Check if there are any values to update
        if (values.size() == 0) {
            return 0;
        }

        // Check if value exists and then perform input validation
        // Note: Input validation not needed for Summary. Any input, including null, is accepted.
        if (values.containsKey(BookContract.BookEntry.COLUMN_NAME)) {
            // Check that the name is not null
            String name = values.getAsString(BookContract.BookEntry.COLUMN_NAME);
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // Check if value exists and then perform input validation
        if (values.containsKey(BookContract.BookEntry.COLUMN_SUMMARY)) {
            // Checks quantity for: NULL and if valid input
            Integer myQuantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (myQuantity.equals(null) || myQuantity < 0) {
                throw new IllegalArgumentException("Quantity Required.");
            }
        }

        // Check if value exists and then perform input validation
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRICE)) {
            // Checks price for : NULL value is allowed (but the database will use a
            // Default of 0 if a value isn't provided). Also, must be a positive value.
            Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_PRICE);
            if (price.equals(null) || price < 0) {
                throw new IllegalArgumentException("Price but be a positive number.");
            }
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Update the new book with the given values
        int rowsAdded = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsAdded != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsAdded;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                rowsDeleted = 0;
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
