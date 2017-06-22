package com.my.dbdemo.db.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.my.dbdemo.db.annotation.Column;
import com.my.dbdemo.db.callback.ConnectionCallback;
import com.my.dbdemo.db.callback.CursorCallback;
import com.my.dbdemo.db.callback.TransactionCallback;
import com.my.dbdemo.db.generate.SQLGenerateHandler;
import com.my.dbdemo.db.io.DBSerializable;
import com.my.dbdemo.db.utils.BeanUtil;
import com.my.dbdemo.db.utils.CommonUtil;
import com.my.dbdemo.db.utils.DBConstant;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库操作类
 * （T：你要操作的实体类）
 * <p>
 * Created by YJH on 2017/3/28 17:06.
 */

public class DBHelper<T extends DBSerializable> extends AbsDBHelper {

    private SQLGenerateHandler<T> genHandler;
    private final String TAG = "DBHelper";
    private final String PRIMARY_KEY = "id";
    private Class<T> clazz;

    public DBHelper(Context context, Class<T> clazz) {
        super(context, DBConstant.DB_NAME, null, DBConstant.DB_VERSION);
        genHandler = new SQLGenerateHandler<>(clazz);
        this.clazz = clazz;
    }

    @Override
    protected String getTableName() {
        return genHandler.getTableName(null);
    }

    @Override
    protected String getCreateSQL() {
        return genHandler.createTable();
    }

    /**
     * 插入实体类
     *
     * @param entity T
     * @return long
     */
    public long insert(T entity) {
        if (null == entity) {
            throw new NullPointerException("插入的实体类不能为空");
        }
        return insert(genHandler.getContentValues(entity));
    }

    /**
     * 插入（有条件的）
     *
     * @param values ContentValues
     * @return long
     */
    private long insert(final ContentValues values) {
        if (null == values || values.size() == 0) {
            return 0;
        }

        return execute(new ConnectionCallback<Long>() {
            @Override
            public Long doInConnection(SQLiteDatabase db) {
                Log.i(TAG, "插入：" + genHandler.getTableName(null) + "，" + values.toString());
                return db.insert(genHandler.getTableName(null), null, values);
            }
        });
    }

    /**
     * 更新一个实体
     *
     * @param entity T
     * @return int
     */
    public int update(T entity) {
        if (null == entity) {
            throw new NullPointerException("entity不能为空");
        }
        //找到这个实体类的id，通过id找到指定行，进行更新
        String[] params = {String.valueOf(BeanUtil.getFieldValue(entity, PRIMARY_KEY))};
        return update(genHandler.getContentValues(entity), PRIMARY_KEY + " = ?", params);
    }

    /**
     * 通过实体类更新数据库中指定的字段
     *
     * @param entity  T
     * @param columns String...
     * @return int
     */
    public int update(T entity, String... columns) {
        if (null == entity) {
            throw new NullPointerException("entity不能为空");
        }
        //找到这个实体类的id，通过id找到指定行，进行更新
        String[] params = {String.valueOf(BeanUtil.getFieldValue(entity, PRIMARY_KEY))};
        return update(genHandler.getContentValues(entity, columns), PRIMARY_KEY + " = ?", params);
    }

    /**
     * 通过id来更新字段
     * columns(列名称)和colVal(列值)顺序必须对应上
     *
     * @param id      id
     * @param columns 列名称
     * @param colVal  列值
     * @return int
     */
    public int update(final long id, String[] columns, String[] colVal) {
        if (columns.length != colVal.length) {
            throw new RuntimeException("列元素和列的长度必须相同");
        }
        ContentValues values = new ContentValues();
        for (int i = 0; i < columns.length; i++) {
            values.put(columns[i], colVal[i]);
        }
        final ContentValues val = values;
        return execute(new ConnectionCallback<Integer>() {
            @Override
            public Integer doInConnection(SQLiteDatabase db) {
                return db.update(genHandler.getTableName(null), val, PRIMARY_KEY + " = ?", new String[]{String.valueOf(id)});
            }
        });
    }

    /**
     * 更新
     *
     * @param values         ContentValues
     * @param whereCondition where后面的条件
     * @param params         对where中?赋值
     * @return int
     */
    public int update(final ContentValues values, final String whereCondition, final String... params) {
        if (null == values || values.size() == 0) {
            return 0;
        }

        return execute(new ConnectionCallback<Integer>() {
            @Override
            public Integer doInConnection(SQLiteDatabase db) {
                Log.i(TAG, "更新：表名：" + genHandler.getTableName(null) + "，ContentValues：" + values.toString() + "，条件：" + whereCondition + "，参数：" + Arrays.toString(params));
                return db.update(genHandler.getTableName(null), values, whereCondition, params);
            }
        });
    }

    /**
     * 删除表内所有数据(并且把sqlite_sequence表中对应的表seq值更改为0)
     *
     * @return int
     */
    public int truncate() {
        int result = delete(null);
        if (result != 0) {
            clearTableId(genHandler.getTableName(null));
        }
        return result;
    }

    /**
     * 通过id删除一行
     *
     * @param id id
     * @return int
     */
    public int deleteById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id必须 >= 0");
        }
        String[] params = {String.valueOf(id)};
        return delete(PRIMARY_KEY + " = ?", params);
    }

    /**
     * 通过entity删除一行
     *
     * @param entity T
     * @return int
     */
    public int deleteByEntity(T entity) {
        if (null == entity) {
            throw new NullPointerException("entity不能为空");
        }
        String[] params = {String.valueOf(BeanUtil.getFieldValue(entity, PRIMARY_KEY))};
        return delete(PRIMARY_KEY + " = ?", params);
    }

    /**
     * 删除
     *
     * @param whereCondition where中的条件
     * @param params         补充where中?的参数
     * @return int
     */
    public int delete(final String whereCondition, final String... params) {
        return execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(SQLiteDatabase db) {
                Log.i(TAG, "删除: 表名：" + genHandler.getTableName(null) + "，whereCondition：" + whereCondition + "，params：" + Arrays.toString(params));
                return db.delete(genHandler.getTableName(null), whereCondition, params);
            }
        });
    }

    public T selectAll(long id) {
//        String s = "select * from " + genHandler.getTableName(null) + " where id = 10";
        String sql = genHandler.selectSQL("id = ?", null);
        String[] params = {String.valueOf(id)};
        return execute(sql, params, new CursorCallback<T>() {
            @Override
            public T doInCursor(Cursor cursor) throws SQLException {
                if (cursor.moveToNext()) {
                    return demo(cursor);
                }
                return null;
            }
        });
    }

    public long selectCount(String whereCondition, String... params) {
        String sql = genHandler.selectSQLCount(new String[]{"count(*)"}, whereCondition);
        return execute(sql, params, new CursorCallback<Long>() {
            @Override
            public Long doInCursor(Cursor cursor) throws SQLException {
                if (cursor.moveToNext()) {
                    // FIXME: 2017/3/31
                    return (long) (cursor.getCount());
                }
                return null;
            }
        });
    }

    private T demo(Cursor cursor) {
//        int size = cursor.getColumnCount();
        T mode = BeanUtil.instanceClass(clazz);
        if (null == mode) {
            return null;
        }

        // 获取并遍历该实体类中所有的字段(包括父类中的所有字段)
        Field[] fieldArr = BeanUtil.getFields(clazz);
        if (fieldArr.length == 0) {
            return null;
        }

        Map<String, Integer> fieldMap = new HashMap<>();
        for (Field field : fieldArr) {
            fieldMap.put(field.getName(), cursor.getColumnIndex(CommonUtil.toUnderline(field.getName())));
        }

        for (Field field : fieldArr) {
            Column column = field.getAnnotation(Column.class);
            if (null == column) {
                continue;
            }

            String fieldName = field.getName(); //获取当前字段的名称
            Class<?> type = field.getType();    //获取当前字段的类型
            //通过获取字段的类型进行赋值
            if (type == String.class) {
                //TEXT类型
                BeanUtil.setFieldValue(mode, fieldName, cursor.getString(fieldMap.get(fieldName))); //为字段赋值
            } else if (type == int.class) {
                //Integer类型
                BeanUtil.setFieldValue(mode, fieldName, cursor.getInt(fieldMap.get(fieldName)));
            } else if (type == long.class) {
                //Integer类型
                BeanUtil.setFieldValue(mode, fieldName, cursor.getLong(fieldMap.get(fieldName)));
            } else if (type == float.class) {
                //REAL类型
                BeanUtil.setFieldValue(mode, fieldName, cursor.getFloat(fieldMap.get(fieldName)));
            } else if (type == double.class) {
                //REAL类型
                BeanUtil.setFieldValue(mode, fieldName, cursor.getDouble(fieldMap.get(fieldName)));
            } else if (type == Integer.class) {
                BeanUtil.setFieldValue(mode, fieldName, cursor.getInt(fieldMap.get(fieldName)));
            } else {
                //BLOB类型
                BeanUtil.setFieldValue(mode, fieldName, cursor.getBlob(fieldMap.get(fieldName)));
            }
        }

        return mode;
    }

}
