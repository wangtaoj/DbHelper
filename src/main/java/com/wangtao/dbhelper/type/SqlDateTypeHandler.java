package com.wangtao.dbhelper.type;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangtao at 2019/1/2 20:33
 */
public class SqlDateTypeHandler extends BaseTypeHandler<Date>{

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Date parameter) throws SQLException {
        ps.setDate(i, parameter);
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getDate(columnName);
    }
}
