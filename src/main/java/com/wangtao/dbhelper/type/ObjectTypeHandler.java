package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangtao at 2019/1/2 20:55
 */
public class ObjectTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, Object parameter) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName);
    }
}
