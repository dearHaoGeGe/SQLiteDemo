package com.my.dbdemo.db.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.my.dbdemo.db.callback.ConnectionCallback;
import com.my.dbdemo.db.callback.CursorCallback;
import com.my.dbdemo.db.callback.OnDoTransaction;
import com.my.dbdemo.db.callback.TransactionCallback;

import java.util.Arrays;

/**
 * Created by YJH on 2017/3/28 9:59.
 */
public abstract class AbsDBHelper extends SQLiteOpenHelper {

    private final String TAG = getClass().getSimpleName();
    protected Context context;

    /**
     * 初始化构造函数
     *
     * @param context Context
     * @param name    数据库名
     * @param factory 游标工厂（基本不用）
     * @param version 版本号
     */
    public AbsDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    protected abstract String getTableName();

    protected abstract String getCreateSQL();

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTable(db);
    }

    /**
     * 当每次打开数据库时被调用
     * getReadableDatabase()和getWritableDatabase()调用
     *
     * @param db SQLiteDatabase
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!isTableExists(db)) {
            Log.i(TAG, "创建表：" + getCreateSQL());
            db.execSQL(getCreateSQL());
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTable(db);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    /**
     * 删除所有表（除了android_metadata，sqlite_sequence）
     *
     * @param db SQLiteDatabase
     */
    private void deleteTable(SQLiteDatabase db) {
        String queryAllTable = "SELECT name FROM sqlite_master WHERE type ='table'";
        String ignoreTable = "android_metadata,sqlite_sequence";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(queryAllTable, null);
            while (cursor.moveToNext()) {
                String table = cursor.getString(0);
                if (ignoreTable.contains(table)) {
                    continue;
                }
                db.execSQL("DROP TABLE IF EXISTS " + table);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "deleteTable: ", e);
            throw new SQLException(e.toString());
        } finally {
            closeSession(null, cursor);
        }
    }

    /**
     * 查看指定的表是否存在
     *
     * @param db SQLiteDatabase
     * @return true存在，false不存在
     */
    private boolean isTableExists(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type ='table' AND name = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{getTableName()});
            if (cursor.moveToNext()) {
                return cursor.getInt(0) > 0;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "isTableExists: ", e);
            throw new SQLException(e.toString());
        } finally {
            closeSession(null, cursor);
        }
        return false;
    }

    /**
     * 清空指定表的id
     * (清空表数据后，在次添加数据时，id是在以前基础上进行加的)
     * UPDATE sqlite_sequence SET seq = 0 WHERE name = 'tableName'
     *
     * @param tableName 表名
     */
    protected void clearTableId(String tableName) {
        SQLiteDatabase db = getWritableDatabase();

        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE sqlite_sequence SET seq = 0 WHERE name = '").append(tableName).append("'");
        try {
            db.execSQL(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "clearTableId: ", e);
            throw new SQLException(e.toString());
        } finally {
            closeSession(db);
        }
    }

    /**
     * 批量做事务
     *
     * @param action OnDoTransaction
     */
    public void execute(OnDoTransaction action) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            action.doInTransaction();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "execute: ", e);
            throw new SQLException(e.toString());
        } finally {
            db.endTransaction();
            closeSession(db);
        }
    }

    public <T> T execute(TransactionCallback<T> action) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            T t = action.doInTransaction(db);
            db.setTransactionSuccessful();
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "execute: ", e);
            throw new SQLException(e.toString());
        } finally {
            db.endTransaction();
            closeSession(db);
        }
    }

    public <T> T execute(ConnectionCallback<T> action) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return action.doInConnection(db);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "execute: ", e);
            throw new SQLException(e.toString());
        } finally {
            closeSession(db);
        }
    }

    public <T> T execute(String sql, String[] params, CursorCallback<T> action) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i(TAG, "查询SQL：" + sql + "，params：" + Arrays.toString(params));
        Cursor cursor = db.rawQuery(sql, params);
        try {
            return action.doInCursor(cursor);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "execute: ", e);
            throw new SQLException(e.toString());
        } finally {
            closeSession(db, cursor);
        }
    }

    protected void closeSession(SQLiteDatabase db) {
        closeSession(db, null);
    }

    protected void closeSession(SQLiteDatabase db, Cursor cursor) {
        if (null != cursor && !cursor.isClosed()) {
            cursor.close();
        }

        if (null == db || db.inTransaction()) {
            return;
        }

        try {
            db.close();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "closeSession: ", e);
            throw new SQLException(e.toString());
        }
    }
}
