package com.my.dbdemo.db.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库列名
 *
 * Created by YJH on 2017/3/25 13:04.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

    /**
     * 类型，默认TEXT
     */
    Type type() default Type.TEXT;

    /**
     * 类型长度，默认0
     */
    int length() default 0;

    /**
     * 是否允许为空，默认true
     */
    boolean isNull() default true;

    /**
     * 是否为主键，默认false
     */
    boolean primary() default false;

    /**
     * 是否自动增长，默认false
     */
    boolean increment() default false;

    enum Type {
        INTEGER,
        TEXT,
        REAL,
        BLOB
    }
}
