package com.my.dbdemo.db.callback;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by YJH on 2017/3/29 16:10.
 */

public interface TransactionCallback<T> {
    T doInTransaction(SQLiteDatabase db);
}
