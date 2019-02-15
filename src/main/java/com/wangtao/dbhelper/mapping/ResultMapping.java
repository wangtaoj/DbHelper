package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeHandler;

/**
 * 注:
 * 如果ResultMap的Type属性指定为Map, 则需要使用javaType以及jdbcType来确定数据库结果字段的类型.
 * 如果没有指定javaType, 那么将会使用ResultSet元数据信息来确定javaType和jdbcType.
 * @author wangtao
 * Created at 2019/1/19 19:07
 */
public class ResultMapping {

    private String column;

    private String property;

    private Class<?> javaType;

    private JdbcType jdbcType;

    private TypeHandler<?> typeHandler;

    ResultMapping() {

    }

    public String getColumn() {
        return column;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public static class Builder {
        private ResultMapping resultMapping = new ResultMapping();

        public Builder(String column, String property) {
            resultMapping.column = column;
            resultMapping.property = property;
        }

        public Builder typeHandler(TypeHandler<?> typeHandler) {
            resultMapping.typeHandler = typeHandler;
            return this;
        }

        public Builder javaType(Class<?> javaType) {
            resultMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            resultMapping.jdbcType = jdbcType;
            return this;
        }

        public ResultMapping build() {
            return resultMapping;
        }
    }
}
