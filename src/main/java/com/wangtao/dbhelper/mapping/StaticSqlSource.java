package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.core.Configuration;

import java.util.List;

/**
 * 静态SqlSource, 在解析配置文件时创建, 不用等到真正运行时.
 * @author wangtao
 * Created at 2019/1/23 9:25
 */
public class StaticSqlSource implements SqlSource {

    private final Configuration configuration;

    private final String sql;

    private final List<ParameterMapping> parameterMappings;

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.configuration = configuration;
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql, parameterMappings, parameterObject);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }
}
