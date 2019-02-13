package com.wangtao.dbhelper.builder;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * @author wangtao
 * Created at 2019/1/10 13:30
 */
public class BuilderException extends PersistenceException {

    private static final long serialVersionUID = 821315641769796200L;

    public BuilderException() {
        super();
    }

    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
