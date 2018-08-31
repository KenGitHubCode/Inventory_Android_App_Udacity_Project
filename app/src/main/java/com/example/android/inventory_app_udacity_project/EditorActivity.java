package com.example.android.inventory_app_udacity_project;

/**
 * Created by Ken Muckey on 8/22/2018.
 */

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory_app_udacity_project.data.BookContract.BookEntry;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Global Variables
    public static final String LOG_TAG = EditorActivity.class.getName();
    // 0 value is arbitrary as there is the only loader in this class
    private static final int URL_LOADER = 0;
    // URI used for edit entry, global for loader callback methods
    private Uri currentEntryUri;
    // Variable to track if the item has changed via OnTouchListener
    private boolean mItemHasChanged = false;

    // EditText field to enter the entry name
    private EditText mNameEditText;
    // EditText field to enter the entry summary
    private EditText mSummaryEditText;
    // EditText field to enter the entry price
    private EditText mPriceEditText;
    // EditText field to enter the entry quantity
    private EditText mQuantityEditText;
    // EditText field to enter the entry supplier
    private EditText mSupplierEditText;
    // EditText field to enter the entry supplier phone number
    private EditText mSupplierPhoneEditText;
    // Buttons
    private Button mDecrementQuantityButton;
    private Button mIncrementQuantityButton;
    private Button mCallSupplier;
    private Button mDeleteEntry;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Use getIntent() and getData() to get the associated URI
        currentEntryUri = getIntent().getData();

        /*
        Only when within the setOnItemClickListener should there be a URI attached to an intent
        Check if uri is null and set title to "Add.."
        else set the title to the "Edit..." text
        */
        if (currentEntryUri == null) {
            setTitle(R.string.add_title_title);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an entry that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_title_title);

            // Init the Cursor Loader
            getLoaderManager().initLoader(URL_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_entry_name);
        mSummaryEditText = (EditText) findViewById(R.id.edit_entry_summary);
        mPriceEditText = (EditText) findViewById(R.id.edit_entry_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_entry_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_entry_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_entry_supplier_phone);

        /*
        setOnTouchListener for each edittext field to determine if changes were made
        to facilitate showUnsavedChangesDialog - preventing users from losing data
        when accidently clicking back/up
        */
        mNameEditText.setOnTouchListener(mTouchListener);
        mSummaryEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        // Assign buttons and then set onClickListners
        mDecrementQuantityButton = (Button) findViewById(R.id.decrement_editor_view_button);
        mIncrementQuantityButton = (Button) findViewById(R.id.increment_editor_view_button);
        mCallSupplier = (Button) findViewById(R.id.call_intent_button);
        mDeleteEntry = (Button) findViewById(R.id.action_delete_button);

    }

    /*
     * Pulls input from fields, assigns to content variable, then inserts into database
     */
    private void saveEntry() {

        //Pull trimmed data into variables
        String nameInput = mNameEditText.getText().toString().trim();
        String summaryInput = mSummaryEditText.getText().toString().trim();
        int quantityInput = 0;
        if (!mQuantityEditText.getText().toString().trim().isEmpty()) {
            quantityInput = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        }
        // Price should default to zero if not entered.  isEmpty check performed to prevent errors
        Double priceInput = 0.0;
        if (!mPriceEditText.getText().toString().trim().isEmpty()) {
            priceInput = Double.parseDouble(mPriceEditText.getText().toString().trim());
        }
        String supplierNameInput = mSupplierEditText.getText().toString().trim();
        String supplierPhoneInput = mSupplierPhoneEditText.getText().toString().trim();

        // Checks if all product values are null or empty and returns to complete activity if so.
        // Assuming user pressed save on mistake, nothing will be saved in this case.
        if (nameInput.isEmpty() &&
                summaryInput.isEmpty() &&
                quantityInput == 0 &&
                priceInput == 0
                ) {
            return;
        }

        // Create new map of values to input into the table
        ContentValues petValues = new ContentValues();
        petValues.put(BookEntry.COLUMN_NAME, nameInput);
        petValues.put(BookEntry.COLUMN_SUMMARY, summaryInput);
        petValues.put(BookEntry.COLUMN_QUANTITY, quantityInput);
        petValues.put(BookEntry.COLUMN_PRICE, priceInput);
        petValues.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameInput);
        petValues.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneInput);

        // Check if uri is null and set title to "Add.."
        // else set the title to the "Edit..." text
        if (currentEntryUri == null) {  // ADD NEW ENTRY instead of update an existing one
            // Insert a new pet into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, petValues);

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

        } else { // UPDATE instead of ADD

            int newInt = getContentResolver().update(currentEntryUri, petValues, null, null);

            // Show a toast message depending on whether or not the insertion was successful
            // Otherwise, the insertion was successful and we can display a toast.
            if (newInt == 1) {
                Toast.makeText(this, getString(R.string.editor_update_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_action_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveEntry();
                //Exit activity, jumping back to previous activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the entry hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // loader callback method onCreateLoader - make sure it is using a URI for one entry.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, currentEntryUri,
                CatalogActivity.PROJECTION, null, null, null);
    }

    // loader callback method onLoadFinished to update the inputs with the data for the entry.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // ensure that the cursor is not null and that it's pointing to the first entry
        if (data == null || !data.moveToFirst()) {
            return;
        }

        // Create and assign variables for the data
        final int currentID = data.getInt(data.getColumnIndex(BookEntry._ID));
        String cursorName = data.getString(data.getColumnIndex(BookEntry.COLUMN_NAME));
        String cursorSummary = data.getString(data.getColumnIndex(BookEntry.COLUMN_SUMMARY));
        final int cursorQuantity = data.getInt(data.getColumnIndex(BookEntry.COLUMN_QUANTITY));
        int cursorPrice = data.getInt(data.getColumnIndex(BookEntry.COLUMN_PRICE));
        String cursorSupplier = data.getString(data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME));
        final String cursorSupplierPhone = data.getString(data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE));

        // Auto populate the Edit Text fields
        mNameEditText.setText(cursorName);
        Log.i(LOG_TAG, "cursor Name is " + cursorName);
        mSummaryEditText.setText(cursorSummary);
        mQuantityEditText.setText(Integer.toString(cursorQuantity));
        mPriceEditText.setText(Integer.toString(cursorPrice));
        mSupplierEditText.setText(cursorSupplier);
        mSupplierPhoneEditText.setText(cursorSupplierPhone);

        // Create onclick listener for a quantity button
        mDecrementQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityDecrement(currentID, cursorQuantity);
            }
        });
        // Create onclick listener for the a quantity button
        mIncrementQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityIncrement(currentID, cursorQuantity);
            }
        });
        // Create onclick listener for the call button
        mCallSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(cursorSupplierPhone);
            }

        });
        // Create onclick listener for the Delete Entry button
        mDeleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }

        });

    }

    //loader callback method onLoaderReset, and clear the input fields.
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mNameEditText.setText("");
        mSummaryEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the entry.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // If the back button is pressed, check for changes and alert user if changes were made
    @Override
    public void onBackPressed() {
        // If the entry hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // If this is a new item, hide the "Delete" menu item.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (currentEntryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the entry.
                deleteEntry();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the entry.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the entry in the database.
     */
    private void deleteEntry() {
        int delInt = getContentResolver().delete(currentEntryUri, null, null);

        // Show a toast message depending on whether or not the getContentResolver().delete method was successful
        if (delInt == 1) {
            // If the int provided by .delete() ==1, then the deletion was successful
            Toast.makeText(this, getString(R.string.editor_delete_entry_successful),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the deletion was unsuccessful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_entry_failed),
                    Toast.LENGTH_SHORT).show();
        }
        //Exit activity, jumping back to previous activity
        finish();
    }

    /**
     * Method to decrease quantity for the item and update the view
     *
     * @param currentBook
     * @param currentBookQuantity
     */
    public void quantityDecrement(int currentBook, int currentBookQuantity) {

        // Decrement the variable passed in by the button listener in the adapter class
        currentBookQuantity--;

        // Rubics: " (include logic so that no negative quantities are displayed)."
        if (currentBookQuantity >= 0) {
            // Create ContentValues for only the quantity field
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, currentBookQuantity);

            // Update the database for only the quantity field
            Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentBook);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
            // Otherwise, the insertion was successful and we can display a toast.
            if (rowsAffected == 1) {
                Toast.makeText(this, R.string.editor_sold_success, Toast.LENGTH_SHORT).show();
            } else {  // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.editor_action_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.no_product_in_stock, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to increase quantity for the item and update the view
     *
     * @param currentBook
     * @param currentBookQuantity
     */
    public void quantityIncrement(int currentBook, int currentBookQuantity) {

        // Decrement the variable passed in by the button listener in the adapter class
        currentBookQuantity++;

        // Create ContentValues for only the quantity field
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_QUANTITY, currentBookQuantity);

        // Update the database for only the quantity field
        Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentBook);
        int rowsAffected = getContentResolver().update(updateUri, values, null, null);
        // Otherwise, the insertion was successful and we can display a toast.
        if (rowsAffected == 1) {
            Toast.makeText(this, R.string.editor_sold_success, Toast.LENGTH_SHORT).show();
        } else {  // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, R.string.editor_action_failed, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Sends the supplier number to the phone app, used DIAL instead of CALL to allow user to decide
     * to make the call or not in the phone app.
     * @param   mSupplierPhoneNumber
     * @returns none
     */
    public void makeCall(String mSupplierPhoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mSupplierPhoneNumber.trim(), null));
        startActivity(intent);
        }

}