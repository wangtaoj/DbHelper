package com.wangtao.dbhelper.type;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * Created by wangtao at 2019/1/2 20:19
 */
public class ResultException extends PersistenceException {

    private static final long serialVersionUID = 6821049855351232211L;

    public ResultException() {
        super();
    }

    public ResultException(String message) {
        super(message);
    }
}
