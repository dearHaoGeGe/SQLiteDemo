package com.my.dbdemo.db.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;

/**
 * Created by YJH on 2017/3/30 14:35.
 */

public class JDBCUtil {

    public static Object getColumnValue(Cursor cursor, int index) {
        SQLiteCursor sc = (SQLiteCursor) cursor;
        int type = sc.getType(index);

        switch (type) {
            case Cursor.FIELD_TYPE_INTEGER:
                return sc.getLong(index);

            case Cursor.FIELD_TYPE_FLOAT:
                return sc.getFloat(index);

            case Cursor.FIELD_TYPE_BLOB:
                return sc.getBlob(index);

            case Cursor.FIELD_TYPE_STRING:
                return sc.getString(index);

            default:
                return null;
        }
    }

}
