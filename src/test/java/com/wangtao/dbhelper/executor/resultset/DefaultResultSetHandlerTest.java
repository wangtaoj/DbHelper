package com.wangtao.dbhelper.executor.resultset;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.core.RowBounds;
import com.wangtao.dbhelper.domian.User;
import com.wangtao.dbhelper.mapping.*;
import com.wangtao.dbhelper.type.IntegerTypeHandler;
import com.wangtao.dbhelper.type.StringTypeHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author wangtao
 * Created at 2019/2/15 16:43
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultResultSetHandlerTest {

    @Mock
    ResultSet rs;

    @Mock
    ResultSetMetaData metaData;

    @Mock
    Statement statement;

    @Test
    public void handlerResultSet_V1() throws SQLException {
        when(statement.getMoreResults()).thenReturn(true);
        when(statement.getUpdateCount()).thenReturn(-1);
        when(statement.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
        when(rs.getString("username")).thenReturn("wangtao");
        when(rs.getInt("age")).thenReturn(21);
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("USERNAME");
        when(metaData.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnClassName(1)).thenReturn(String.class.getName());
        when(metaData.getColumnLabel(2)).thenReturn("AGE");
        when(metaData.getColumnType(2)).thenReturn(Types.INTEGER);
        when(metaData.getColumnClassName(2)).thenReturn(Integer.class.getName());

        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(getMappedStatement(), RowBounds.DEFAULT);
        List<User> users = resultSetHandler.handleResultSet(statement);
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("wangtao", user.getUsername());
        assertEquals(21, user.getAge().intValue());
    }

    @Test
    public void handlerResultSet_V2() throws SQLException {
        when(statement.getMoreResults()).thenReturn(true);
        when(statement.getUpdateCount()).thenReturn(-1);
        when(statement.getResultSet()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
        when(rs.getString("USERNAME")).thenReturn("wangtao");
        when(rs.getInt("AGE")).thenReturn(21);
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("USERNAME");
        when(metaData.getColumnType(1)).thenReturn(Types.VARCHAR);
        when(metaData.getColumnClassName(1)).thenReturn(String.class.getName());
        when(metaData.getColumnLabel(2)).thenReturn("AGE");
        when(metaData.getColumnType(2)).thenReturn(Types.INTEGER);
        when(metaData.getColumnClassName(2)).thenReturn(Integer.class.getName());

        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(getMappedStatementByResultType(), RowBounds.DEFAULT);
        List<User> users = resultSetHandler.handleResultSet(statement);
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("wangtao", user.getUsername());
        assertEquals(21, user.getAge().intValue());
    }


    MappedStatement getMappedStatement() {
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        SqlSource sqlSource = new StaticSqlSource("select username, age from user", new ArrayList<>());
        ResultMap resultMap = new ResultMap.Builder("BaseResultMap", User.class)
                .resultMappings(new ArrayList<ResultMapping>(){
                    private static final long serialVersionUID = -1866939844247169205L;
                    {
                        add(new ResultMapping.Builder("username", "username")
                                .typeHandler(new StringTypeHandler())
                                .build());
                        add(new ResultMapping.Builder("age", "age")
                                .typeHandler(new IntegerTypeHandler())
                                .build());
                    }
                }).build();
        return new MappedStatement.Builder(configuration, "com.wangtao.dao.UserMapper.findById", sqlSource)
                .resultMap(resultMap)
                .build();
    }

    MappedStatement getMappedStatementByResultType() {
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        SqlSource sqlSource = new StaticSqlSource("select username, age from user", new ArrayList<>());
        ResultMap resultMap = new ResultMap.Builder("com.wangtao.dao.UserMapper.findById-Inline", User.class)
                .resultMappings(new ArrayList<>()).build();
        return new MappedStatement.Builder(configuration, "com.wangtao.dao.UserMapper.findById", sqlSource)
                .resultMap(resultMap)
                .build();
    }
}
