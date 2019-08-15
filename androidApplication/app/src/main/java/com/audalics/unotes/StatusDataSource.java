package com.audalics.unotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexb on 6/11/2017.
 */

public class StatusDataSource {
    private SQLiteDatabase db;
    private StatusOpenHelper helper;
    private String[] columns;

    public StatusDataSource(Context context) {
        helper = new StatusOpenHelper(context);
    }

    public void open(boolean readOnly) throws SQLException {
        if (readOnly) {
            db = helper.getReadableDatabase();
        } else {
            db = helper.getWritableDatabase();
        }
    }

    public void close() {
        helper.close();
    }

    public Status createStatus(String name, String desc, int color, int progress) {
        ContentValues values = new ContentValues();
        values.put(StatusOpenHelper.COLUMN_NAME, name);
        values.put(StatusOpenHelper.COLUMN_DESCRIPTION, desc);
        values.put(StatusOpenHelper.COLUMN_COLOR, color);
        values.put(StatusOpenHelper.COLUMN_PROGRESS, progress);
        long insertID = db.insert(StatusOpenHelper.TABLE_NAME, null, values);
        Cursor cursor = db.query(StatusOpenHelper.TABLE_NAME, columns, StatusOpenHelper.COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        Status newStatus = cursorToStatus(cursor);
        cursor.close();
        return newStatus;
    }

    public Status readStatus(long id) {
        return this.cursorToStatus(db.rawQuery("SELECT * FROM " + helper.TABLE_NAME + " WHERE " + helper.COLUMN_ID + " = ?", new String[]{ String.valueOf(id) }));
    }

    public void updateStatus(long id, String name, String desc, int color, int progress) {
        ContentValues values = new ContentValues();
        values.put(StatusOpenHelper.COLUMN_NAME, name);
        values.put(StatusOpenHelper.COLUMN_DESCRIPTION, desc);
        values.put(StatusOpenHelper.COLUMN_COLOR, color);
        values.put(StatusOpenHelper.COLUMN_PROGRESS, Math.min(Math.max(0, progress), 100));
        db.update(helper.TABLE_NAME, values, helper.COLUMN_ID + "=" + String.valueOf(id), null);
    }

    public void deleteStatus(Status status) {
        long id = status.getId();
        db.delete(StatusOpenHelper.TABLE_NAME, StatusOpenHelper.COLUMN_ID + " = " + id, null);
    }

    public long getNumStatuses() {
        List<Status> statuses = new ArrayList<Status>();
        Cursor cursor = db.rawQuery("SELECT count(*) from " + StatusOpenHelper.TABLE_NAME, null);
        return cursor.getLong(0);
    }

    public List<Status> getAllStatuses() {
        List<Status> statuses = new ArrayList<Status>();
        Cursor cursor = db.query(StatusOpenHelper.TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Status status = cursorToStatus(cursor);
            statuses.add(status);
            cursor.moveToNext();
        }
        cursor.close();
        return statuses;
    }

    private Status cursorToStatus(Cursor cursor) {
        Status status = new Status();
        status.setId(cursor.getLong(0));
        status.setName(cursor.getString(1));
        status.setDescription(cursor.getString(2));
        status.setColor(cursor.getInt(3));
        status.setProgress(cursor.getInt(4));
        return status;
    }
}
