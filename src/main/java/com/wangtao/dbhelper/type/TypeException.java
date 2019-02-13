package com.wangtao.dbhelper.type;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * Created by wangtao at 2019/1/2 16:58
 */
public class TypeException extends PersistenceException {

    private static final long serialVersionUID = -3523447953873915571L;

    public TypeException() {
        super();
    }

    public TypeException(String message) {
        super(message);
    }

    public TypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
