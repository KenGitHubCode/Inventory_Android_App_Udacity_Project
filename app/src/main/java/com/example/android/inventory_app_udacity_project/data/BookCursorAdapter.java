package com.example.android.inventory_app_udacity_project.data;

/**
 * Created by Ken Muckey on 8/21/2018.
 */


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory_app_udacity_project.CatalogActivity;
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
    public void bindView(View view, final Context context, Cursor cursor) {

        // Assign descriptive variable name to view
        View listItemView = view;

        // Initialize the list_item text views
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name_list_view);
        TextView summaryTextView = (TextView) listItemView.findViewById(R.id.summary);
        TextView quantityTextView = (TextView) listItemView.findViewById(R.id.quantity_list_item);
        TextView priceTextView = (TextView) listItemView.findViewById(R.id.price_list_view);
        TextView supplierTextView = (TextView) listItemView.findViewById(R.id.edit_entry_supplier_phone);
        TextView supplierPhoneTextView = (TextView) listItemView.findViewById(R.id.edit_entry_supplier_phone);
        Button purchaseButton = (Button) listItemView.findViewById(R.id.sold_list_item);

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
        int summaryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUMMARY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        // Use that index to extract the String or Int value of the word
        // at the current row the cursor is on.
        final int currentID = cursor.getInt(idColumnIndex);
        String currentName = cursor.getString(nameColumnIndex);
        String currentSummary = cursor.getString(summaryColumnIndex);
        double currentPrice = cursor.getDouble(priceColumnIndex);
        final int currentQuantity = cursor.getInt(quantityColumnIndex);

        // Bind the variables to the views
        nameTextView.setText(currentName);
        if (currentSummary.isEmpty()) {
            currentSummary = context.getString(R.string.not_available);
        }
        summaryTextView.setText(currentSummary);
        priceTextView.setText("$" + String.valueOf(currentPrice));
        quantityTextView.setText("Qty: " + String.valueOf(currentQuantity));
        // Note that there is no need to bind the sold button

        // Create onclick listener for the Sold button
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogActivity Activity = (CatalogActivity) context;
                Activity.quantityDecrement(currentID, currentQuantity);
            }

        });
    };
}

