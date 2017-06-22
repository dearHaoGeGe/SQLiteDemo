package com.my.dbdemo.db.io;

import java.io.Serializable;

/**
 * Created by YJH on 2017/3/25 13:23.
 */

public interface BaseSerializable<T> extends Serializable {
    T getId();

    void setId(T id);
}
