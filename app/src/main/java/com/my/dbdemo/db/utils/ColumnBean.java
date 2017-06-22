package com.my.dbdemo.db.utils;

import com.my.dbdemo.db.annotation.Column;

/**
 * 列元素的实体类
 *
 * Created by YJH on 2017/3/25 23:02.
 */

public class ColumnBean {

    private String value;
    private Column.Type type;
    private int length;
    private boolean isNull;
    private boolean primary;
    private boolean increment;

    public ColumnBean(String fieldName, Column column) {
        this.value = CommonUtil.toUnderline(fieldName); //把column转换成下划线方式
        this.type = column.type();
        this.length = column.length();
        this.isNull = column.isNull();
        this.primary = column.primary();
        this.increment = column.increment();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Column.Type getType() {
        return type;
    }

    public void setType(Column.Type type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

    @Override
    public String toString() {
        return "ColumnBean{" +
                "value='" + value + '\'' +
                ", type=" + type +
                ", length=" + length +
                ", isNull=" + isNull +
                ", primary=" + primary +
                ", increment=" + increment +
                '}';
    }
}
