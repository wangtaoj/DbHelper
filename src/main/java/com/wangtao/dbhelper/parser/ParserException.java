package com.wangtao.dbhelper.parser;

import com.wangtao.dbhelper.exception.PersistenceException;

/**
 * @author wangtao
 * Created at 2019/1/17 13:54
 */
public class ParserException extends PersistenceException {

    private static final long serialVersionUID = 3112150738975858093L;

    public ParserException() {
        super();
    }

    public ParserException(String message) {
        super(message);
    }
}
