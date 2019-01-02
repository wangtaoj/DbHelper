package com.wangtao.dbhelper.type;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created by wangtao at 2019/1/2 20:41
 */
public class LocalDateHandler extends BaseTypeHandler<LocalDate> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, LocalDate parameter) throws SQLException {
        ps.setDate(i, Date.valueOf(parameter));
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Date result = rs.getDate(columnName);
        return (result == null) ? null : result.toLocalDate();
    }
}
