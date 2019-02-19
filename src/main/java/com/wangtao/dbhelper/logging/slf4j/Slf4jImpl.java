package com.wangtao.dbhelper.logging.slf4j;

import com.wangtao.dbhelper.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangtao
 * Created at 2019/2/19 15:29
 */
public class Slf4jImpl implements Log {

    private Logger log;

    public Slf4jImpl(String name) {
        log = LoggerFactory.getLogger(name);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }

    @Override
    public void trace(String s) {
        log.trace(s);
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }
}
