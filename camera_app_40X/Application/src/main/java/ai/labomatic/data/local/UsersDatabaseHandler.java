package ai.labomatic.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ai.labomatic.data.model.User;

/**
 * DATABASE MODEL
 */

public class UsersDatabaseHandler extends SQLiteOpenHelper{

    // Database name
    private static final String DATABASE_NAME = "ManageUsers";

    // Db version
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_USERS = "users";

    // Columns of table users
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    public UsersDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FOLDERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD + " TEXT UNIQUE);";
        db.execSQL(CREATE_FOLDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table folders if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Create tables again
        onCreate(db);
    }

    /**
     * CRUD operations (Create, read, update, delete).
     * */
    public void createUser(User user){
        // Create db
        SQLiteDatabase db = this.getWritableDatabase();
        // Tuple that holds new values
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        // Create row
        db.insert(TABLE_USERS, null, values);
        // Close db
        db.close();
    }

    public List<User> readAllUsers(){
        // List of users
        List<User> allUsers = new ArrayList<User>();
        // Create db and query
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                    user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                    allUsers.add(user);
                } while(cursor.moveToNext());
            }
        } else{
            // Do nothing
        }
        // Close db
        db.close();
        return allUsers;
    }

    public User readUserByEmail(String email){
        // Create an empty user object
        User user = new User();
        // Create database and query
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_EMAIL + " = " + email;
        // Execute query
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            }
        } else{
            // Do nothing
        }
        db.close();
        return user;
    }

    public User readUserById(int id){
        // Reference db and create query
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_ID + " = " + String.valueOf(id);
        // User instance
        User user = new User();
        // Read query
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to position
        if (cursor != null){
            if (cursor.moveToFirst()){
                user.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            }
        } else{
            // Do nothing
        }
        // CLose db
        db.close();
        return user;
    }

    public int updateUser(User user){
        // Reference database
        SQLiteDatabase db = this.getWritableDatabase();
        // Tuple that holds new ai.labomatic.data
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, user.getPassword());
        // Update row in db
        int result = db.update(TABLE_USERS, values, KEY_EMAIL + " = ?",
                new String[]{String.valueOf(user.getId())});
        // Close db
        db.close();
        return result;
    }

    public void deleteUser(User user){
        // Create db
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete row
        db.delete(TABLE_USERS, KEY_EMAIL + "=?",
                new String[]{String.valueOf(user.getEmail())});
        // Close db
        db.close();
    }

    public void dropTableUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.close();
    }

}
