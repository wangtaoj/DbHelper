package com.wangtao.dbhelper.exception;

/**
 * Created by wangtao at 2018/12/20 16:36
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = 3879581623614732360L;

    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
