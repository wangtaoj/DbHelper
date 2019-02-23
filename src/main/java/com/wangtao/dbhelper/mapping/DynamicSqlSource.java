package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.builder.SqlSourceBuilder;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.scripting.xmltags.DynamicContext;
import com.wangtao.dbhelper.scripting.xmltags.SqlNode;

import java.util.Map;

/**
 * 动态Sqlsource: 存在$占位符或者动态标签的SQL.
 * 需要从参数中获取值, 因此在运行时解析SQL语句.
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
        // 拼接SQL, 处理动态标签, $占位符.
        DynamicContext context = new DynamicContext(configuration, parameterObject);
        rootSqlNode.apply(context);

        // 创建静态SqlSource, 处理#占位符.
        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder(configuration);
        SqlSource sqlSource = sqlSourceBuilder.parse(context.getSql());

        // 添加额外参数.
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
            if (!DynamicContext.PARAMETER.equals(entry.getKey())) {
                boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
            }
        }
        return boundSql;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public SqlNode getRootSqlNode() {
        return rootSqlNode;
    }
}
