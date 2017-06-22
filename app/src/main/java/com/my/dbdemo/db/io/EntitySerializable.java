package com.my.dbdemo.db.io;

import com.my.dbdemo.db.annotation.Column;

/**
 * Created by YJH on 2017/3/25 13:29.
 */

public class EntitySerializable implements DBSerializable {

    @Column(type = Column.Type.INTEGER, isNull = false, primary = true, increment = true)
    private long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
