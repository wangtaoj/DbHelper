package com.wangtao.dbhelper.mapping;

import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/22 10:57
 */
public class BoundSql {

    private final String sql;

    private final List<ParameterMapping> parameterMappings;

    private final Object parameter;

    public BoundSql(String sql, List<ParameterMapping> parameterMappings, Object parameter) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameter = parameter;
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public Object getParameter() {
        return parameter;
    }
}
