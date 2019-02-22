package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.scripting.ExpressionEvaluator;

/**
 * @author wangtao
 * Created at 2019/2/22 14:25
 */
public class IfSqlNode implements SqlNode {

    private SqlNode sqlNode;

    private String test;

    public IfSqlNode(SqlNode sqlNode, String test) {
        this.sqlNode = sqlNode;
        this.test = test;
    }

    @Override
    public void apply(DynamicContext context) {
        if (ExpressionEvaluator.evaluateBoolean(context.getBindings(), test)) {
            sqlNode.apply(context);
        }
    }
}
