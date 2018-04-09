package ai.labomatic.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ai.labomatic.data.model.Setting;

/**
 * Created by root on 2/9/18.
 */

public class SettingsDatabaseHandler extends SQLiteOpenHelper {
    // All Static variables

    private static final String TAG = "SettingsDatabase";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Settings";

    // Folders table name
    private static final String TABLE_SETTINGS = "settings";

    // Folders Table Columns names
    private static final String KEY_ID_SETTING = "id";
    private static final String KEY_NAME_SETTING = "configName";
    private static final String KEY_VALUE = "configValue";

    public SettingsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create folders table
        String CREATE_FOLDERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS + "("
                + KEY_ID_SETTING + " INTEGER PRIMARY KEY, " + KEY_NAME_SETTING + " TEXT UNIQUE, "
                + KEY_VALUE + " TEXT)";
        db.execSQL(CREATE_FOLDERS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table folders if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        // Create tables again
        onCreate(db);
    }

    /**
     * CRUD operations for the settings database.
     * */
    // Create setting
    public void createSetting(Setting setting) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_SETTING, setting.getName()); // Setting name
        values.put(KEY_VALUE, setting.getValue()); // Setting value
        db.insert(TABLE_SETTINGS, null, values);
        db.close(); // Closing database connection
    }

    // Read single setting
    public Setting readSettingByName(String settingName){
        Setting sett = new Setting();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + KEY_NAME_SETTING
                + " = '" + settingName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                sett.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_SETTING)));
                sett.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME_SETTING)));
                sett.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
            }
        } else {
            // nothing
        }
        db.close();
        return sett;
    }

    // Read all configurations
    public List<Setting> readAllSettings() {
        List<Setting> allSettings = new ArrayList<Setting>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Setting sett = new Setting();
                    sett.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID_SETTING)));
                    sett.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME_SETTING)));
                    sett.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
                    // Adding folder to list
                    allSettings.add(sett);
                } while (cursor.moveToNext());
            }
        } else{
            return allSettings;
        }
        db.close();
        // return contact list
        return allSettings;
    }

    // Update
    public int updateSetting(Setting setting) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.i(TAG, "Updating: " + setting.getName() + ", " + String.valueOf(setting.getValue()));
        ContentValues values = new ContentValues();
        values.put(KEY_VALUE, setting.getValue());
        int result = db.update(TABLE_SETTINGS, values,
                KEY_ID_SETTING+"="+setting.getId(),
                null);
        db.close();
        return result;
    }

    // Remove setting
    public int deleteSettingByName(String settingName){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_NAME_SETTING + "=?";
        String[] whereArgs = new String[]{String.valueOf(settingName)};
        int result = db.delete(TABLE_SETTINGS, whereClause, whereArgs);
        db.close();
        return result;
    }

    // Drop table
    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SETTINGS, null, null);
        db.close();
    }

}
