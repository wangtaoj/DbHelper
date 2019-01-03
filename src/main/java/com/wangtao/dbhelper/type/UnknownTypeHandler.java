package com.wangtao.dbhelper.type;

import com.wangtao.dbhelper.core.Resources;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by wangtao at 2019/1/3 15:23
 */
public class UnknownTypeHandler extends BaseTypeHandler<Object> {

    private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();

    private TypeHandlerRegistry typeHandlerRegistry;

    public UnknownTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
        this.typeHandlerRegistry = typeHandlerRegistry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setNotNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) {
        TypeHandler typeHandler = resolveTypeHandler(parameter, jdbcType);
        typeHandler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Object parameter) {
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) {
        TypeHandler<?> typeHandler = resolveTypeHandler(rs, columnName);
        return typeHandler.getResult(rs, columnName);
    }

    private TypeHandler<?> resolveTypeHandler(Object paramter, JdbcType jdbcType) {
        if (paramter == null) {
            return OBJECT_TYPE_HANDLER;
        }
        TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(paramter.getClass(), jdbcType);
        if (typeHandler == null || typeHandler instanceof UnknownTypeHandler) {
            return OBJECT_TYPE_HANDLER;
        }
        return typeHandler;
    }

    private TypeHandler<?> resolveTypeHandler(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取总列数
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                if (rsmd.getColumnName(i).equals(columnName)) {
                    return resolveTypeHandler(rsmd, i);
                }
            }
            return OBJECT_TYPE_HANDLER;
        } catch (SQLException e) {
            throw new TypeException("获取javaType以及jdbcType出现错误, 列名: " + columnName + ". 具体原因: " + e);
        }
    }

    private TypeHandler<?> resolveTypeHandler(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
        Class<?> javaType = safeGetJavaType(rsmd.getColumnClassName(columnIndex));
        JdbcType jdbcType = safeGetJdbctype(rsmd.getColumnType(columnIndex));
        TypeHandler<?> typeHandler = null;
        if (javaType != null) {
            typeHandler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
        } else if (jdbcType != null) {
            typeHandler = typeHandlerRegistry.getTypeHandler(jdbcType);
        }
        if (typeHandler == null || typeHandler instanceof UnknownTypeHandler) {
            return OBJECT_TYPE_HANDLER;
        }
        return typeHandler;
    }

    private Class<?> safeGetJavaType(String className) {
        try {
            return Resources.classForName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private JdbcType safeGetJdbctype(int typeCode) {
        return JdbcType.forCode(typeCode);
    }
}
