package com.wangtao.dbhelper.datasource;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * Created by wangtao at 2018/12/21 15:58
 */
public class DataSourceException extends PersistenceException {

    private static final long serialVersionUID = 8858699921735074905L;

    public DataSourceException() {
        super();
    }

    public DataSourceException(String message) {
        super(message);
    }
}
