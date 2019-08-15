package com.audalics.unotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexb on 6/11/2017.
 */

public class UserDataSource {
    private SQLiteDatabase db;
    private UserOpenHelper helper;
    private String[] columns;

    public UserDataSource(Context context) {
        helper = new UserOpenHelper(context);
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

    public Status createUser(String name, String desc, int color, int profile_img_ref) {
        ContentValues values = new ContentValues();
        values.put(UserOpenHelper.COLUMN_NAME, name);
        values.put(UserOpenHelper.COLUMN_DESCRIPTION, desc);
        values.put(UserOpenHelper.COLUMN_COLOR, color);
        values.put(UserOpenHelper.COLUMN_PROFILE_IMG_REF, profile_img_ref);
        long insertID = db.insert(UserOpenHelper.TABLE_NAME, null, values);
        Cursor cursor = db.query(UserOpenHelper.TABLE_NAME, columns, UserOpenHelper.COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        Status newStatus = cursorToStatus(cursor);
        cursor.close();
        return newStatus;
    }

    public Status readStatus(long id) {
        return this.cursorToStatus(db.rawQuery("SELECT * FROM " + helper.TABLE_NAME + " WHERE " + helper.COLUMN_ID + " = ?", new String[]{ String.valueOf(id) }));
    }

    public void updateUser(long id, String name, String desc, int color, int profile_img_ref) {
        ContentValues values = new ContentValues();
        values.put(UserOpenHelper.COLUMN_NAME, name);
        values.put(UserOpenHelper.COLUMN_DESCRIPTION, desc);
        values.put(UserOpenHelper.COLUMN_COLOR, color);
        values.put(UserOpenHelper.COLUMN_PROFILE_IMG_REF, profile_img_ref);
        db.update(helper.TABLE_NAME, values, helper.COLUMN_ID + "=" + String.valueOf(id), null);
        int c = Color.BLACK;
    }

    public void deleteUser(Status status) {
        long id = status.getId();
        db.delete(UserOpenHelper.TABLE_NAME, UserOpenHelper.COLUMN_ID + " = " + id, null);
    }

    public long getNumStatuses() {
        List<Status> statuses = new ArrayList<Status>();
        Cursor cursor = db.rawQuery("SELECT count(*) from " + UserOpenHelper.TABLE_NAME, null);
        return cursor.getLong(0);
    }

    public List<Status> getAllStatuses() {
        List<Status> statuses = new ArrayList<Status>();
        Cursor cursor = db.query(UserOpenHelper.TABLE_NAME, columns, null, null, null, null, null);
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
