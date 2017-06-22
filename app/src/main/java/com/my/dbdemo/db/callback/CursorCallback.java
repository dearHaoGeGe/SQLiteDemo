package com.my.dbdemo.db.callback;

import android.database.Cursor;

import java.sql.SQLException;

/**
 * Created by YJH on 2017/3/30 11:02.
 */

public interface CursorCallback<T> {
    T doInCursor(Cursor cursor) throws SQLException;
}
