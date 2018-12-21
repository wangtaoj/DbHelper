package com.wangtao.dbhelper.exception;

/**
 * Created by wangtao at 2018/12/20 16:37
 */
public class TooManyResultException extends PersistenceException {

    private static final long serialVersionUID = 530328993834001806L;

    public TooManyResultException() {
        super();
    }

    public TooManyResultException(String message) {
        super(message);
    }
}
