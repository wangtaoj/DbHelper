package com.wangtao.dbhelper.mapping;

/**
 * @author wangtao
 * Created at 2019/1/22 10:57
 */
public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);
}
