package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.builder.SqlSourceBuilder;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.scripting.xmltags.DynamicContext;
import com.wangtao.dbhelper.scripting.xmltags.SqlNode;

/**
 * 静态SqlSource实现.
 * @author wangtao
 * Created at 2019/1/22 16:31
 */
public class RawSqlSource implements SqlSource {

    /**
     * StaticSqlSource实例
     */
    private final SqlSource sqlSource;

    public RawSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        String sql = getSql(configuration, rootSqlNode);
        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder(configuration);
        this.sqlSource = sqlSourceBuilder.parse(sql);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private String getSql(Configuration configuration, SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(configuration, null);
        rootSqlNode.apply(context);
        return context.getSql();
    }
}
