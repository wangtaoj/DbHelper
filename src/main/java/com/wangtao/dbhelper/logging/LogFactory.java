package com.wangtao.dbhelper.logging;

import com.wangtao.dbhelper.logging.slf4j.Slf4jImpl;

/**
 * @author wangtao
 * Created at 2019/2/19 15:26
 */
public final class LogFactory {

    public static Log getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Log getLogger(String name) {
        return new Slf4jImpl(name);
    }
}
