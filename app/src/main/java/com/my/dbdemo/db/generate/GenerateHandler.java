package com.my.dbdemo.db.generate;

import android.content.ContentValues;

import com.my.dbdemo.db.io.DBSerializable;

/**
 * Created by YJH on 2017/3/28 14:28.
 */

public interface GenerateHandler<T extends DBSerializable> {

    /**
     * 创建表SQL语句
     *
     * @return String
     */
    String createTable();

    /**
     * 获取表名
     *
     * @param alias 别名
     * @return String
     */
    String getTableName(String alias);

    /**
     * 把实体类中的加注解的所有元素都put进去
     *
     * @param t T
     * @return ContentValues
     */
    ContentValues getContentValues(T t);

    /**
     * 指定实体类中的元素put进去
     * （把columns中的参数put）
     *
     * @param t       T
     * @param columns String[]
     * @return ContentValues
     */
    ContentValues getContentValues(T t, String[] columns);

    /**
     * 相当于：where column1 = xxx column2 = xxx...
     *
     * @param condition 条件
     * @return String
     */
    String where(String condition);

    /**
     * 排序：ORDER BY ASC(升序) 或 DESC(降序)
     *
     * @param sort ASC  或  DESC
     * @return String
     */
    String order(String sort);

    /**
     * 限制：从第几条查，从第几条往下查几条
     *
     * @param pageNumber 第几页
     * @param pageSize   一页多大
     * @return String
     */
    String limit(int pageNumber, int pageSize);

    /**
     * 生成列名
     *
     * @param prefix  前缀
     * @param columns 要生成的列名,忽略生成全部
     * @return String
     */
    String getColumn(String prefix, String... columns);

    /**
     * 生成 select 语句
     *
     * @param whereCondition where中的条件
     * @param sort           排序
     * @return String
     */
    String selectSQL(String whereCondition, String sort);

    /**
     * 查询条数(如果columns里面放表的列名也可以查最后一条的数据)
     *
     * @param columns        count(*) + 列数组
     * @param whereCondition where中的条件
     * @return String
     */
    String selectSQLCount(String[] columns, String whereCondition);
}
