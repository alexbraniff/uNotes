package com.audalics.unotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by alexb on 6/11/2017.
 */

public class NoteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "unotes.db";
    public static final String TABLE_NAME = "note";

    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS_ID = "status_id";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_PARENT_ID = "parent_id";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_UUID + " TEXT PRIMARY KEY," +
                    ");";

    private static final String TABLE_DROP =
            "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    NoteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NoteOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL(TABLE_DROP);
        onCreate(db);
    }
}
