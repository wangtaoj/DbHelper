package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.builder.SqlSourceBuilder;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.scripting.xmltags.DynamicContext;
import com.wangtao.dbhelper.scripting.xmltags.SqlNode;

/**
 * @author wangtao
 * Created at 2019/1/22 15:34
 */
public class DynamicSqlSource implements SqlSource {

    private final Configuration configuration;

    private final SqlNode rootSqlNode;

    public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        this.configuration = configuration;
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        DynamicContext context = new DynamicContext(configuration, parameterObject);
        rootSqlNode.apply(context);
        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder(configuration);
        SqlSource sqlSource = sqlSourceBuilder.parse(context.getSql());
        return sqlSource.getBoundSql(parameterObject);
    }
}
