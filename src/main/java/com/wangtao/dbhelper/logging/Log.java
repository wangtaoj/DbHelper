package com.wangtao.dbhelper.logging;

/**
 * @author wangtao
 * Created at 2019/2/19 15:26
 */
public interface Log {

    boolean isDebugEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);
}
