package com.wangtao.dbhelper.transaction;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * @author wangtao
 * Created at 2019/2/13 16:10
 */
public class TransactionException extends PersistenceException {

    private static final long serialVersionUID = -7317538191361518919L;

    public TransactionException() {
        super();
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
