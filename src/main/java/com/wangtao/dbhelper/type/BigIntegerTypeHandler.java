package com.wangtao.dbhelper.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wangtao
 * Created at 2019/2/13 11:23
 */
public class BigIntegerTypeHandler extends BaseTypeHandler<BigInteger> {

    @Override
    public void setNotNullParameter(PreparedStatement ps, int i, BigInteger parameter) throws SQLException {
        ps.setBigDecimal(i, new BigDecimal(parameter));
    }

    @Override
    public BigInteger getNullableResult(ResultSet rs, String columnName) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnName);
        return value == null ? null : value.toBigInteger();
    }
}
