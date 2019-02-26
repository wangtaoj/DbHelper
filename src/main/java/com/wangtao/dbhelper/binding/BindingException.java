package com.wangtao.dbhelper.binding;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * @author wangtao
 * Created at 2019/2/25 14:41
 */
public class BindingException extends PersistenceException {

    private static final long serialVersionUID = 3151834494686106371L;

    public BindingException() {
        super();
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }
}
