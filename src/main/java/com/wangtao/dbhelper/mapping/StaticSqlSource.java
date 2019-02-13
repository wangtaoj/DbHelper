package com.wangtao.dbhelper.mapping;

import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/23 9:25
 */
public class StaticSqlSource implements SqlSource {

    private final String sql;

    private final List<ParameterMapping> parameterMappings;

    public StaticSqlSource(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(sql, parameterMappings, parameterObject);
    }
}
