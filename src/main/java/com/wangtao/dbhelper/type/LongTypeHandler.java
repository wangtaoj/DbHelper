package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangtao at 2019/1/3 15:01
 */
public class LongTypeHandler extends BaseTypeHandler<Long> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Long parameter) throws SQLException {
        ps.setLong(i, parameter);
    }

    @Override
    public Long getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long result = rs.getLong(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }
}
