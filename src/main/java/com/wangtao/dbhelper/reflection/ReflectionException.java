package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * Created by wangtao at 2018/12/26 15:03
 */
public class ReflectionException extends PersistenceException {

    private static final long serialVersionUID = -1008330969274113507L;

    public ReflectionException() {
        super();
    }

    public ReflectionException(String message) {
        super(message);
    }
}
