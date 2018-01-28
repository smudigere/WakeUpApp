package com.fashiontechwakeup.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This database helper class will store the users information if provided.
 */
public class RemindersDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "a_i.db";
    private static final String TABLE_NAME = "reminders";

    public RemindersDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (notification_no INTEGER PRIMARY KEY, reminder TEXT, expiry TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert data into account_table sqlite table.
     */
    void insertData(int id, String reminder, String expiry) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("notification_no", id);
            contentValues.put("reminder", reminder);
            contentValues.put("expiry", expiry);

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE notification_no = " + id, null);

            if (cursor.getCount() == 0)
                db.insert(TABLE_NAME, null, contentValues);
            else
                db.update(TABLE_NAME, contentValues, "id = " + id, null);

            cursor.close();

            logging("insert");
        } catch (Exception e)   {
            e.printStackTrace();
        }
    }

    private void logging(String a)  {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("Get events" + a, DatabaseUtils.dumpCursorToString(db.rawQuery("select * from " + TABLE_NAME, null)));
    }

    /**
     * If the cursor returns any data of this table, the tutorial page will not be opened again.
     * @return  <p  The cursor returned holds the information about the user, if logged in.    />
     */
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        String cursorString = DatabaseUtils.dumpCursorToString(res);
        res.moveToFirst();
        db.close();
        return res;
    }

    /**
     * Method to get all the events.
     *
     * @return  <p> Events, and details of the event. </p>
     */
    public List<List<String>> getEvents()   {

        try {

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

            if (cursor != null) {
                cursor.moveToFirst();
                List<List<String>> events = new ArrayList<>();
                do {

                    List<String> eventData = new ArrayList<>();
                    eventData.add(cursor.getString(cursor.getColumnIndex("notification_no")));
                    eventData.add(cursor.getString(cursor.getColumnIndex("reminder")));
                    eventData.add(cursor.getString(cursor.getColumnIndex("expiry")));

                    events.add(eventData);
                } while (cursor.moveToNext());
                cursor.close();

                return events;
            }
        } catch (Exception e)   {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteEntry(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE notification_no = " + id);
        Log.i("DELETE ENTRY", String.valueOf(id));
        logging("delete");
        db.close();
    }

}