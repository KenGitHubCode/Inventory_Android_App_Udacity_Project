package com.example.android.inventory_app_udacity_project.data;

/**
 * Created by Ken Muckey on 8/21/2018.
 */


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory_app_udacity_project.R;

import static com.example.android.inventory_app_udacity_project.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //  Return the list item view
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the entry data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Assign descriptive variable name to view
        View listItemView = view;

        // Initialize the list_item text views
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
        TextView summaryTextView = (TextView) listItemView.findViewById(R.id.summary);

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
        int summaryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUMMARY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

        // Use that index to extract the String or Int value of the word
        // at the current row the cursor is on.
        int currentID = cursor.getInt(idColumnIndex);
        String currentName = cursor.getString(nameColumnIndex);
        String currentSummary = cursor.getString(summaryColumnIndex);
        int currentPrice = cursor.getInt(priceColumnIndex);
        int currentQuantity = cursor.getInt(quantityColumnIndex);
        String CurrentSupplierName = cursor.getString(supplierNameColumnIndex);
        String CurrentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

        // Bind the variables to the views
        nameTextView.setText(currentName);
        if (currentSummary.isEmpty()) {
            currentSummary = "No Summary Available";
        }
        summaryTextView.setText(currentSummary);

    }
}

