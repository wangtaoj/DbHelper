package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangtao at 2019/1/3 14:55
 */
public class BooleanTypeHandler extends BaseTypeHandler<Boolean> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Boolean parameter) throws SQLException {
        ps.setBoolean(i, parameter);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        boolean result = rs.getBoolean(columnName);
        return (!result && rs.wasNull()) ? null : result;
    }
}
