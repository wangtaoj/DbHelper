package com.wangtao.dbhelper.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by wangtao at 2019/1/2 16:16
 */
public interface TypeHandler<T> {

    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType);

    /**
     * 从结果集中获取指定列名的值
     * @param rs 结果集
     * @param columnName 列名, 不是别名
     * @return 对应的Java值
     */
    T getResult(ResultSet rs, String columnName);
}
