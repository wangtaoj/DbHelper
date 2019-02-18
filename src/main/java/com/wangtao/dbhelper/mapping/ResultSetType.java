package com.wangtao.dbhelper.mapping;

import java.sql.ResultSet;

/**
 * @author wangtao
 * Created at 2019/2/18 21:42
 */
public enum ResultSetType {

    DEFAULT(-1),
    FORWARD_ONLY(ResultSet.TYPE_FORWARD_ONLY),
    SCROLL_INSENSITIVE(ResultSet.TYPE_SCROLL_INSENSITIVE),
    SCROLL_SENSITIVE(ResultSet.TYPE_SCROLL_SENSITIVE);

    private int value;

    ResultSetType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
