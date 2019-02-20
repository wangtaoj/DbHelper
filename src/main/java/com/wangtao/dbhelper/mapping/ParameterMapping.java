package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeHandler;

/**
 * 参数映射
 * @author wangtao
 * Created at 2019/1/22 10:39
 */
public class ParameterMapping {

    private String property;

    private JdbcType jdbcType;

    /**
     * 如果#{}表达式没有指定typeHandler, typeHandler = null
     */
    private TypeHandler<?> typeHandler;

    ParameterMapping() {

    }

    public String getProperty() {
        return property;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public static class Builder {

        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(String property) {
            parameterMapping.property = property;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            parameterMapping.typeHandler = typeHandler;
            return this;
        }

        public ParameterMapping build() {
            return parameterMapping;
        }
    }
}
