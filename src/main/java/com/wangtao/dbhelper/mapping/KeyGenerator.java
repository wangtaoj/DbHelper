package com.wangtao.dbhelper.mapping;

import java.sql.Statement;

/**
 * @author wangtao
 * Created at 2019/2/19 13:32
 */
public interface KeyGenerator {

    /**
     * 将自增主键设置到参数中.
     * @param ms MappedStatement对象
     * @param statement Statement对象
     * @param parameter 参数
     */
    void processAfter(MappedStatement ms, Statement statement, Object parameter);
}
