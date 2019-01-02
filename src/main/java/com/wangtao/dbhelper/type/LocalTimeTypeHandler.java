package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

/**
 * Created by wangtao at 2019/1/2 20:44
 */
public class LocalTimeTypeHandler extends BaseTypeHandler<LocalTime> {
    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, LocalTime parameter) throws SQLException {
        ps.setTime(i, Time.valueOf(parameter));
    }

    @Override
    public LocalTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Time result = rs.getTime(columnName);
        return (result == null) ? null : result.toLocalTime();
    }
}
