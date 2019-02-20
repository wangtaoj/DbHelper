package com.wangtao.dbhelper.executor;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * @author wangtao
 * Created at 2019/2/13 16:51
 */
public class ExecutorException extends PersistenceException {

    private static final long serialVersionUID = -1167228701416058162L;

    public ExecutorException() {
    }

    public ExecutorException(String message) {
        super(message);
    }

    public ExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
