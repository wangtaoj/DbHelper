package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangtao at 2019/1/2 16:45
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) {
        if(parameter == null) {
            if(jdbcType == null) {
                throw new TypeException("对于参数为null时, 需要指定一个JdbcType, 在设置第" + i + "个参数出现异常");
            }
            try {
                ps.setNull(i, jdbcType.getCode());
            } catch (SQLException e) {
                throw new TypeException(String.format("错误的设置参数, 参数位置: %d, jdbcType: %d",
                        i, jdbcType.getCode()));
            }
        }
        try {
            setNotNullParameter(ps, i, parameter, jdbcType);
        } catch (SQLException e) {
            throw new TypeException(String.format("错误的设置参数, 参数位置: %d, jdbcType: %d",
                    i, jdbcType.getCode()));
        }
    }

    @Override
    public T getResult(ResultSet rs, String columnName) {
        try {
            return getNullableResult(rs, columnName);
        } catch (SQLException e) {
            throw new ResultException("获取结果集异常, 出现异常的列名: " + columnName);
        }
    }

    public abstract void setNotNullParameter(PreparedStatement ps, int i, T parameter) throws SQLException;

    public void setNotNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        setNotNullParameter(ps, i, parameter);
    }

    public abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;
}
