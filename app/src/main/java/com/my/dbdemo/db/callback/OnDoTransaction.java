package com.my.dbdemo.db.callback;

/**
 * 批量数据做事务
 *
 * Created by YJH on 2017/3/28 21:01.
 */

public interface OnDoTransaction {

    /**
     * 批量插入/删除
     */
    void doInTransaction();

}
