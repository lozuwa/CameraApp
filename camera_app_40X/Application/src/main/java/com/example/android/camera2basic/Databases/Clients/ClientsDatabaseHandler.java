package com.example.android.camera2basic.Databases.Clients;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/5/18.
 * Clients database ORM handler.
 */

public class ClientsDatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ClientsManager";

    // Folders table name
    private static final String TABLE_FOLDERS = "Folders";

    // Folders Table Columns names
    private static final String KEY_ID_FOLDERS = "id";
    private static final String KEY_NAME_FOLDERS = "folderName";
    private static final String KEY_COMPLETED_FOLDERS = "completed";

    // Images table name
    private static String TABLE_IMAGES = "Images";

    // Images Table Columns names
    private static final String KEY_ID_IMAGES = "id";
    private static final String KEY_NAME_FOLDERS = "";

    public ClientsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FOLDERS + "("
                + KEY_ID_FOLDERS + " INTEGER PRIMARY KEY," + KEY_NAME_FOLDERS + " TEXT,"
                + KEY_COMPLETED_FOLDERS + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // create folder
    void createFolder(Folders folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_FOLDERS, folder.getfolderName()); // Contact Name
        values.put(KEY_COMPLETED_FOLDERS, folder.getCompleted()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_FOLDERS, null, values);
        db.close(); // Closing database connection
    }

    // read folder
    public Folders getFolder(int id) {
        Folders folder = new Folders();
        String selectQuery = "SELECT * FROM " + TABLE_FOLDERS + " WHERE " + KEY_ID_FOLDERS + " = "
                + String.valueOf(id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
            folder.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_FOLDERS)));
            folder.setfolderName(cursor.getString(cursor.getColumnIndex(KEY_NAME_FOLDERS)));
            folder.setCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED_FOLDERS)));
        } else{

        }
        db.close();
        // return folder
        return folder;
    }

    // read folder by name
    public Folders getFolderByName(String name){
        Folders folder = new Folders();
        String selectQuery = "SELECT * FROM " + TABLE_FOLDERS + " WHERE " + KEY_NAME_FOLDERS + " = "
                + String.valueOf(name);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
            folder.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_FOLDERS)));
            folder.setfolderName(cursor.getString(cursor.getColumnIndex(KEY_NAME_FOLDERS)));
            folder.setCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED_FOLDERS)));
        } else {

        }
        db.close();
        return folder;
    }

    // Reading All folders
    public List<Folders> getAllFolders() {
        List<Folders> allFolders = new ArrayList<Folders>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FOLDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Folders folder = new Folders();
                    folder.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_FOLDERS)));
                    folder.setfolderName(cursor.getString(cursor.getColumnIndex(KEY_NAME_FOLDERS)));
                    folder.setCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED_FOLDERS)));
                    // Adding folder to list
                    allFolders.add(folder);
                } while (cursor.moveToNext());
            }
        } else{
            return allFolders;
        }
        db.close();
        // return contact list
        return allFolders;
    }

    // Updating single contact
    public int updateFolder(Folders folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_FOLDERS, folder.getfolderName());
        values.put(KEY_COMPLETED_FOLDERS, folder.getCompleted());

        // updating row
        int result =  db.update(TABLE_FOLDERS, values, KEY_ID_FOLDERS + " = ?",
                new String[] { String.valueOf(folder.getId()) });
        db.close();
        return result;
    }

    // Deleting single contact
    public void deleteFolder(Folders folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_FOLDERS, KEY_ID_FOLDERS + " = ?",
                    new String[] { String.valueOf(folder.getId()) });
        } catch (Exception e){
            Log.e("TAG::", "Value could not be deleted, it might not exist.");
        } finally {
            db.close();
        }
    }

    public int getFoldersCount() {
        // Select all the rows
        String countQuery = "SELECT  * FROM " + TABLE_FOLDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();
        // return count
        return cursor.getCount();
    }

    public void dropTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOLDERS, null, null);
        db.close();
    }

}
