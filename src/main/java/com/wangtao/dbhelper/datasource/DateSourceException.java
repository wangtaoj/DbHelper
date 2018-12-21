package com.wangtao.dbhelper.datasource;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * Created by wangtao at 2018/12/21 15:58
 */
public class DateSourceException extends PersistenceException {

    public DateSourceException() {
        super();
    }

    public DateSourceException(String message) {
        super(message);
    }
}
