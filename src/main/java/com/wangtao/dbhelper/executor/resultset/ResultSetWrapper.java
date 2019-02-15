package com.wangtao.dbhelper.executor.resultset;

import com.wangtao.dbhelper.mapping.ResultMap;
import com.wangtao.dbhelper.type.JdbcType;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 记录ResultSet结果集中的元数据信息.
 * 比如: 列名, 对应的Java类型, JdbcType等等.
 * @author wangtao
 * Created at 2019/2/15 9:43
 */
public class ResultSetWrapper {

    private ResultSet resultSet;

    /**
     * 列名
     **/
    private List<String> columnNames = new ArrayList<>();

    /**
     * 列名对应的Java类型(完全限定名)
     **/
    private List<String> classNames = new ArrayList<>();

    /**
     * 列名对应的JdbcType
     **/
    private Map<String, JdbcType> jdbcTypes = new HashMap<>();

    /**
     * 映射的列名
     * key: resultMap的id
     **/
    private Map<String, List<String>> mappedColumnNamesMap = new HashMap<>();

    /**
     * 未映射的列名
     * key: resultMap的id
     **/
    private Map<String, List<String>> unMappedColumnNamesMap = new HashMap<>();

    public ResultSetWrapper(ResultSet rs) throws SQLException {
        this.resultSet = rs;
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            // 列别名
            String columnName = metaData.getColumnLabel(i);
            columnNames.add(columnName);
            // 列对应的Java类型(完全限定名)
            classNames.add(metaData.getColumnClassName(i));
            // 列对应的JdbcType
            jdbcTypes.put(columnName.toUpperCase(Locale.ENGLISH), JdbcType.forCode(metaData.getColumnType(i)));
        }
    }

    /**
     * 获取列名对应的JdbcType
     * @param columnName 列名
     * @return 返回列名对应的JdbcType
     */
    public JdbcType getJdbcType(String columnName) {
        return jdbcTypes.get(columnName.toUpperCase(Locale.ENGLISH));
    }

    /**
     * 获取已经映射过的列名列表
     * @param resultMap resultMap对象
     * @return 列名列表
     */
    public List<String> getMappedColumnNames(ResultMap resultMap) {
        List<String> mappedColumnNames = mappedColumnNamesMap.get(resultMap.getId());
        if (mappedColumnNames == null) {
            loadColumnNames(resultMap);
            return mappedColumnNamesMap.get(resultMap.getId());
        } else {
            return mappedColumnNames;
        }
    }

    /**
     * 获取未映射过的列名列表
     * @param resultMap resultMap对象
     * @return 列名列表
     */
    public List<String> getUnMappedColumnNames(ResultMap resultMap) {
        List<String> unMappedColumnNames = unMappedColumnNamesMap.get(resultMap.getId());
        if(unMappedColumnNames == null) {
            loadColumnNames(resultMap);
            return mappedColumnNamesMap.get(resultMap.getId());
        } else {
            return unMappedColumnNames;
        }
    }

    private void loadColumnNames(ResultMap resultMap) {
        List<String> mappedColumnNames = new ArrayList<>();
        List<String> unMappedColumnNames = new ArrayList<>();
        for (String columnName : columnNames) {
            String upperColumnName = columnName.toUpperCase();
            Set<String> mappedColumnSet = resultMap.getMappedColumns().stream()
                    .map(v -> v.toUpperCase(Locale.ENGLISH)).collect(Collectors.toSet());
            if(mappedColumnSet.contains(upperColumnName)) {
                mappedColumnNames.add(columnName);
            } else {
                unMappedColumnNames.add(columnName);
            }
        }
        mappedColumnNamesMap.put(resultMap.getId(), mappedColumnNames);
        unMappedColumnNamesMap.put(resultMap.getId(), unMappedColumnNames);
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public List<String> getClassNames() {
        return classNames;
    }
}
