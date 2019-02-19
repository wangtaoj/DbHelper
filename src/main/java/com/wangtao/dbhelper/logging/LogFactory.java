package com.wangtao.dbhelper.logging;

import com.wangtao.dbhelper.logging.slf4j.Slf4jImpl;

/**
 * @author wangtao
 * Created at 2019/2/19 15:26
 */
public final class LogFactory {

    Log getLog(Class<?> clazz) {
        return new Slf4jImpl(clazz.getName());
    }
}
