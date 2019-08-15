package com.audalics.unotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by alexb on 6/11/2017.
 */

public class StatusOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "unotes.db";
    public static final String TABLE_NAME = "status";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_PROGRESS = "progress";

    private static final String TABLE_CREATE =
        "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + "           INTEGER PRIMARY KEY," +
            COLUMN_NAME + "         TEXT," +
            COLUMN_DESCRIPTION + "  TEXT," +
            COLUMN_COLOR + "        INTEGER," +
            COLUMN_PROGRESS + "     INTEGER" +
        ");";

    private static final String TABLE_DROP =
        "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    StatusOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(StatusOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL(TABLE_DROP);
        onCreate(db);
    }
}
