package com.example.android.camera2basic;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HP on 23/10/2017.
 */

public class DbFeed {

    /** Constants */
    public static final String TABLE_NAME = "pfmDb";
    public static final String COLUMN_NAME_TITLE = "FOLDER_NAME";

    /** SQLite instance */
    public SQLiteDatabase mydatabase;

    public DbFeed() {
        //mydatabase = openOrCreateDatabase(DbFeed.TABLE_NAME, MODE_PRIVATE, null);
    }

}
