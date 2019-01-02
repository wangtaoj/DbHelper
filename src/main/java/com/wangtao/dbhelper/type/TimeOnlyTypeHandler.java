package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

/**
 * Created by wangtao at 2019/1/2 20:53
 */
public class TimeOnlyTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Date parameter) throws SQLException {
        ps.setTime(i, new Time(parameter.getTime()));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Time result = rs.getTime(columnName);
        return (result == null) ? null : new Date(result.getTime());
    }
}
