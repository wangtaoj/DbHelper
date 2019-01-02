package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by wangtao at 2019/1/2 20:46
 */
public class DateTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Date parameter) throws SQLException {
        ps.setTimestamp(i, new Timestamp(parameter.getTime()));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp result = rs.getTimestamp(columnName);
        return (result == null) ? null : new Date(result.getTime());
    }
}


