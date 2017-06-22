package com.my.dbdemo.db.callback;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by YJH on 2017/3/28 21:28.
 */

public interface ConnectionCallback<T> {
    T doInConnection(SQLiteDatabase db);
}
