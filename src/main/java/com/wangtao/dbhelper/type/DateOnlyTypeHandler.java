package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by wangtao at 2019/1/2 20:49
 */
public class DateOnlyTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Date parameter) throws SQLException {
        ps.setDate(i, new java.sql.Date(parameter.getTime()));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        java.sql.Date result = rs.getDate(columnName);
        return (result == null) ? null : new Date(result.getTime());
    }
}
