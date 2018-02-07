package com.example.android.camera2basic.Databases.Users;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HP on 23/10/2017.
 */

public class User {

    /** Constants */
    public static final String TABLE_NAME = "pfmDb";
    public static final String COLUMN_NAME_TITLE = "FOLDER_NAME";
    
    public User() {
        //mydatabase = openOrCreateDatabase(User.TABLE_NAME, MODE_PRIVATE, null);
    }

}
