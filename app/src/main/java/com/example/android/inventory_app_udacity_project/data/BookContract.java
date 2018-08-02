package com.example.android.inventory_app_udacity_project.data;

import android.provider.BaseColumns;

/**
 * Created by Ken Muckey on 8/1/2018.
 */
public final class BookContract {
    /*
     * Innner class for each table in the database
     * Implements BaseColumns class
     */
    public static abstract class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "Product_Name";
        public static final String COLUMN_PRICE = "Product_Price";
        public static final String COLUMN_QUANTITY = "Product_Quantity";
        public static final String COLUMN_SUPPLIER_NAME = "Supplier_Name";
        public static final String COLUMN_SUPPLIER_PHONE = "Supplier_Phone_Number";


    }
}