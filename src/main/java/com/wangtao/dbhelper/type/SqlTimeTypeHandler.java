package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Created by wangtao at 2019/1/2 20:34
 */
public class SqlTimeTypeHandler extends BaseTypeHandler<Time> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Time parameter) throws SQLException {
        ps.setTime(i, parameter);
    }

    @Override
    public Time getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getTime(columnName);
    }
}
