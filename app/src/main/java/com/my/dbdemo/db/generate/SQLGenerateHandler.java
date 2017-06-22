package com.my.dbdemo.db.generate;

import android.content.ContentValues;
import android.text.TextUtils;

import com.my.dbdemo.db.annotation.Column;
import com.my.dbdemo.db.annotation.TableName;
import com.my.dbdemo.db.io.DBSerializable;
import com.my.dbdemo.db.utils.BeanUtil;
import com.my.dbdemo.db.utils.ColumnBean;
import com.my.dbdemo.db.utils.CommonUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * SQL语句生成器
 * <p>
 * Created by YJH on 2017/3/28 14:30.
 */

public class SQLGenerateHandler<T extends DBSerializable> implements GenerateHandler<T> {

    /**
     * 表名
     */
    private String tableName = null;

    /**
     * 用于存放列名与字段名的映射关系
     */
    private Map<String, ColumnBean> mapFields;


    public SQLGenerateHandler(Class<T> clazz) {
        mapFields = new LinkedHashMap<>();
        initTable(clazz);
        initColumn(clazz);
    }

    /**
     * 获取表名
     *
     * @param clazz Class
     */
    private void initTable(Class clazz) {
        if (clazz.isAnnotationPresent(TableName.class)) {
            //若已存在，则使用该注解中定义的表名
            TableName table = (TableName) clazz.getAnnotation(TableName.class);
            tableName = table.value();
        }
        if (TextUtils.isEmpty(tableName)) {
            // 若不存在，则将实体类名转换为下划线风格的表名
            tableName = CommonUtil.toUnderline(clazz.getSimpleName());
        }
    }

    /**
     * 获取类中的字段(包括父类中的所有字段)，遍历有Column注解的，获取之后放到map中
     *
     * @param clazz Class
     */
    private void initColumn(Class clazz) {
        // 获取并遍历该实体类中所有的字段(包括父类中的所有字段)
        Field[] fields = BeanUtil.getFields(clazz);
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (null == column) {
                continue;
            }
            mapFields.put(field.getName(), new ColumnBean(field.getName(), column));
        }
    }


    @Override
    public String createTable() {
        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        builder.append(tableName).append(" (");
        builder.append(getColumnElement());
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String getTableName(String alias) {
        if (TextUtils.isEmpty(alias)) {
            return tableName;
        } else {
            return tableName + " AS " + alias;
        }
    }

    private String getColumnElement() {
        StringBuilder build = new StringBuilder();
        Collection<ColumnBean> columns = mapFields.values();
        for (ColumnBean bean : columns) {
            build.append(bean.getValue()).append(" ").append(bean.getType());
            if (bean.getLength() > 0) {
                build.append("(").append(bean.getLength()).append(")");
            }
            if (bean.isPrimary()) {
                build.append(" PRIMARY KEY");
            }
            if (bean.isIncrement()) {
                build.append(" AUTOINCREMENT");
            }
            if (!bean.isNull()) {
                build.append(" NOT NULL");
            }
            build.append(",");
        }
        build.delete(build.length() - 1, build.length());
        return build.toString();
    }

    @Override
    public ContentValues getContentValues(T t) {
        return getContentValues(t, null);
    }

    @Override
    public ContentValues getContentValues(T t, String[] columns) {
        if (null == mapFields || mapFields.isEmpty()) {
            return null;
        }
        ContentValues values = new ContentValues();
        Set<ColumnBean> colSet = appointColumns(columns);
        for (ColumnBean bean : colSet) {
            String name = bean.getValue();
            String colName = CommonUtil.toHump(name);
            Object val = BeanUtil.getFieldValue(t, colName);    //通过反射获取实体类中对应字段的值
            if (null == val || ("id".equals(name)) && ((Long) val) == 0L) {
                continue;
            }
            values.put(name, val.toString());
        }
        return values;
    }

    @Override
    public String where(String whereCondition) {
        if (TextUtils.isEmpty(whereCondition)) {
            return "";
        }
        return " WHERE " + whereCondition;
    }

    @Override
    public String order(String sort) {
        if (TextUtils.isEmpty(sort)) {
            return "";
        }
        return " ORDER BY " + sort;
    }

    /**
     * select * from 表名 where 条件 order by 排序 limit 开始位置, 从开始位置往下查几条
     */
    @Override
    public String limit(int pageNumber, int pageSize) {
        int ps = (pageNumber - 1) * pageSize;
        StringBuilder builder = new StringBuilder();
        builder.append(" LIMIT ").append(ps).append(", ").append(pageSize);
        return builder.toString();
    }

    @Override
    public String getColumn(String prefix, String... columns) {
        StringBuilder builder = new StringBuilder();
        Set<ColumnBean> colSet = appointColumns(columns);
        for (ColumnBean col : colSet) {
            if (TextUtils.isEmpty(prefix)) {
                builder.append(col.getValue());
            } else {
                builder.append(prefix).append(".").append(col.getValue());
            }
            builder.append(",");
        }
        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }

    @Override
    public String selectSQL(String whereCondition, String sort) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ").append(getColumn(null)).append(" FROM ").append(tableName);
        builder.append(where(whereCondition));
        builder.append(order(sort));
        return builder.toString();
    }

    @Override
    public String selectSQLCount(String[] columns, String whereCondition) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ").append(getFunColumn(columns)).append(" FROM ").append(tableName);
        builder.append(where(whereCondition));
        return builder.toString();
    }

    /**
     * 指定列
     * (把需要指定的列写到columns)
     *
     * @param columns String...
     * @return Set<ColumnBean>
     */
    private Set<ColumnBean> appointColumns(String... columns) {
        Set<ColumnBean> colSet = new HashSet<>();
        if (null == columns || columns.length == 0) {
            colSet.addAll(mapFields.values());
        } else {
            for (String col : columns) {
                String colName = CommonUtil.toHump(col);
                ColumnBean colBean = mapFields.get(colName);
                if (null == colBean) {
                    continue;
                }
                colSet.add(colBean);
            }
        }
        return colSet;
    }

    /**
     * SQL语句列名字段的拼接
     *
     * @param columns 列名
     * @return String
     */
    private String getFunColumn(String... columns) {
        StringBuilder build = new StringBuilder();

        for (String col : columns) {
            build.append(col).append(",");
        }
        build.delete(build.length() - 1, build.length());
        return build.toString();
    }
}
