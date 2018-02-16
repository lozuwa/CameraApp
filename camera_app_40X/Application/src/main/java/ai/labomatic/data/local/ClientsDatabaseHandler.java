package ai.labomatic.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ai.labomatic.data.model.Folder;
import ai.labomatic.data.model.Image;

/**
 * Created by root on 2/5/18.
 * Clients database ORM handler.
 */

public class ClientsDatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "Clients";

    // Folders table name
    private static final String TABLE_FOLDERS = "folders";

    // Folders Table Columns names
    private static final String KEY_ID_FOLDERS = "id";
    private static final String KEY_NAME_FOLDERS = "folderName";

    // Images table name
    private static final String TABLE_IMAGES = "images";

    // IMAGES TABLE Columns names
    private static final String KEY_ID_IMAGES = "id";
    private static final String KEY_NAME_IMAGES = "imageName";
    private static final String KEY_FOLDER_NAME_IMAGES = KEY_NAME_FOLDERS;

    public ClientsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create folders table
        String CREATE_FOLDERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FOLDERS + "("
        + KEY_ID_FOLDERS + " INTEGER PRIMARY KEY," + KEY_NAME_FOLDERS + " TEXT UNIQUE);";
        db.execSQL(CREATE_FOLDERS_TABLE);
        // Create images table
        String CREATE_IMAGES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_IMAGES + "("
            + KEY_ID_IMAGES + " INTEGER PRIMARY KEY," + KEY_NAME_IMAGES + " TEXT,"
            + KEY_FOLDER_NAME_IMAGES + " TEXT)";
        db.execSQL(CREATE_IMAGES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table folders if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
        // Drop older table images if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        // Create tables again
        onCreate(db);
    }

    /**
     * Folders CRUD(Create, Read, Update, Delete) operations.
     */
    // create folder
    public void createFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_FOLDERS, folder.getfolderName()); // Folder name

        // Inserting Row
        db.insert(TABLE_FOLDERS, null, values);
        db.close(); // Closing database connection
    }

    // read folder
    public Folder readFolderById(int id) {
        Folder folder = new Folder();
        String selectQuery = "SELECT * FROM " + TABLE_FOLDERS + " WHERE "
                + KEY_ID_FOLDERS + " = " + String.valueOf(id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                folder.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_FOLDERS)));
                folder.setfolderName(cursor.getString(cursor.getColumnIndex(KEY_NAME_FOLDERS)));
            }
        } else{

        }
        db.close();
        // return folder
        return folder;
    }

    // read folder by name
    public Folder readFolderByName(String folderName){
        Folder folder = new Folder();
        String selectQuery = "SELECT * FROM " + TABLE_FOLDERS + " WHERE " + KEY_NAME_FOLDERS + " = '"
                + folderName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
            folder.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_FOLDERS)));
            folder.setfolderName(cursor.getString(cursor.getColumnIndex(KEY_NAME_FOLDERS)));
        } else {

        }
        db.close();
        return folder;
    }

    // Reading All folders
    public List<Folder> readAllFolders() {
        List<Folder> allFolders = new ArrayList<Folder>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FOLDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Folder folder = new Folder();
                    folder.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_FOLDERS)));
                    folder.setfolderName(cursor.getString(cursor.getColumnIndex(KEY_NAME_FOLDERS)));
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

    // Updating single folder
    public int updateFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_FOLDERS, folder.getfolderName());

        // updating row
        int result =  db.update(TABLE_FOLDERS, values, KEY_ID_FOLDERS + " = ?",
                new String[] { String.valueOf(folder.getId()) });
        db.close();
        return result;
    }

    // Deleting single folder
    public void deleteFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = TABLE_FOLDERS;
        String whereClause = KEY_NAME_FOLDERS + "=?";
        String[] whereArgs = new String[] { String.valueOf(folder.getfolderName()) };
        db.delete(table, whereClause, whereArgs);
        db.close();
    }

    public void dropFoldersTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOLDERS, null, null);
        db.close();
    }

    /**
     * Images CRUD(Create, Read, Update, Delete) operations.
     */

    // Create image
    public void createImage(Image image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_IMAGES, image.getImageName());
        values.put(KEY_FOLDER_NAME_IMAGES, image.getFolderName());
        db.insert(TABLE_IMAGES, null, values);
        db.close();
    }

    // Read all images
    public List<Image> readAllImages(){
        // Create list of images
        List<Image> allImages = new ArrayList<Image>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Image image = new Image();
                    image.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_IMAGES)));
                    image.setImageName(cursor.getString(cursor.getColumnIndex(KEY_NAME_IMAGES)));
                    image.setFolderName(cursor.getString(cursor.getColumnIndex(KEY_FOLDER_NAME_IMAGES)));
                    // Adding folder to list
                    allImages.add(image);
                } while (cursor.moveToNext());
            }
        } else{
            return allImages;
        }
        db.close();
        // return contact list
        return allImages;
    }

    // Read image by id
    public Image readImageById(int id){
        Image image = new Image();
        String query = "SELECT * FROM " + TABLE_IMAGES
                + " WHERE " + KEY_ID_IMAGES + " = " + String.valueOf(id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null){
            // just pass
        } else{
            cursor.moveToFirst();
            image.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_IMAGES)));
            image.setImageName(cursor.getString(cursor.getColumnIndex(KEY_NAME_IMAGES)));
            image.setFolderName(cursor.getString(cursor.getColumnIndex(KEY_FOLDER_NAME_IMAGES)));
        }
        db.close();
        return image;
    }

    // Read image by name
    public Image readImageByName(String imageName){
        Image image = new Image();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_IMAGES + " WHERE " + KEY_NAME_IMAGES
                + " = '" + imageName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null){
            // Do nothing
        } else {
            cursor.moveToFirst();
            image.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_IMAGES)));
            image.setImageName(cursor.getString(cursor.getColumnIndex(KEY_NAME_IMAGES)));
            image.setFolderName(cursor.getString(cursor.getColumnIndex(KEY_FOLDER_NAME_IMAGES)));
        }
        db.close();
        return image;
    }

    // Read all images associated with a specific folder name
    public List<Image> readImagesByFolderName(String folderName){
        // Data structure to hold a list of images
        List<Image> imgs = new ArrayList<Image>();
        // Database
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_IMAGES + " WHERE "
                + KEY_NAME_FOLDERS + " = '" + folderName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    Image img = new Image();
                    img.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_IMAGES)));
                    img.setImageName(cursor.getString(cursor.getColumnIndex(KEY_NAME_IMAGES)));
                    img.setFolderName(cursor.getString(cursor.getColumnIndex(KEY_FOLDER_NAME_IMAGES)));
                    imgs.add(img);
                } while (cursor.moveToNext());
            } else {
                // do nothing
            }
        } else{
            // do nothing
        }
        // Close db
        db.close();
        // Return the images
        return imgs;
    }

    public String[] readColumnsImagesTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db = getReadableDatabase();
        Cursor dbCursor = db.query(TABLE_IMAGES, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        db.close();
        return columnNames;
    }

    // Delete image
    public void deleteImage(Image image){
        SQLiteDatabase db = this.getWritableDatabase();
        String table = TABLE_IMAGES;
        String whereClause = KEY_NAME_IMAGES + "=?";
        String[] whereArgs = new String[] { String.valueOf(image.getImageName()) };
        db.delete(table, whereClause, whereArgs);
        db.close();
    }

    // Update image
    public int updateImage(Image image){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_IMAGES, image.getImageName());
        values.put(KEY_FOLDER_NAME_IMAGES, image.getFolderName());

        // updating row
        int result =  db.update(TABLE_FOLDERS, values, KEY_ID_FOLDERS + " = ?",
                new String[] { String.valueOf(image.getId()) });
        db.close();
        return result;
    }

    // Drop images table
    public void dropImagesTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGES, null, null);
        db.close();
    }

}
