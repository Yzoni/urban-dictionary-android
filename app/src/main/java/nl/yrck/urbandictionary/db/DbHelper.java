package nl.yrck.urbandictionary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nl.yrck.urbandictionary.db.models.SearchHistoryItem;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "udDatabase";

    private static final String TABLE_SEARCH_HISTORY = "search_history";

    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_MODIFIED_AT = "created_at";

    private static final String KEY_SEARCH_HISTORY_TERM = "search_term";

    private static final String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE "
            + TABLE_SEARCH_HISTORY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SEARCH_HISTORY_TERM
            + " TEXT," + KEY_CREATED_AT + " DATETIME," + KEY_MODIFIED_AT + "DATETIME" + ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SEARCH_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long createSearchHistoryItem(String searchTerm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCH_HISTORY_TERM, searchTerm);
        values.put(KEY_CREATED_AT, getDateTime());
        values.put(KEY_MODIFIED_AT, getDateTime());

        return db.insert(TABLE_SEARCH_HISTORY, null, values);
    }

    public List<SearchHistoryItem> listSearchHistoryItems() {
        List<SearchHistoryItem> searchHistoryItems = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_SEARCH_HISTORY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                SearchHistoryItem td = new SearchHistoryItem(
                        c.getInt((c.getColumnIndex(KEY_ID))),
                        c.getString(c.getColumnIndex(KEY_SEARCH_HISTORY_TERM)),
                        c.getString(c.getColumnIndex(KEY_CREATED_AT))
                );

                searchHistoryItems.add(td);
            } while (c.moveToNext());
        }
        c.close();

        return searchHistoryItems;
    }

    public boolean searchHistoryItemExists(String searchTerm) {
        String query = "SELECT * FROM " + TABLE_SEARCH_HISTORY + " WHERE " + KEY_SEARCH_HISTORY_TERM + "=? LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, new String[]{searchTerm});
        int exists = c.getCount();
        c.close();
        return 0 != exists;
    }

    public void dropSearchHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEARCH_HISTORY, null, null);
    }

    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
