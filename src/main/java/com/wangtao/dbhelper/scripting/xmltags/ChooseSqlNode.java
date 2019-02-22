package com.wangtao.dbhelper.scripting.xmltags;

import java.util.List;

/**
 * @author wangtao
 * Created at 2019/2/22 21:22
 */
public class ChooseSqlNode implements SqlNode {

    private SqlNode defaultSqlNode;

    /** IfSqlNode **/
    private List<SqlNode> whenSqlNodes;

    public ChooseSqlNode(List<SqlNode> whenSqlNodes, SqlNode defaultSqlNode) {
        this.whenSqlNodes = whenSqlNodes;
        this.defaultSqlNode = defaultSqlNode;
    }

    @Override
    public boolean apply(DynamicContext context) {
        if (whenSqlNodes != null) {
            for (SqlNode sqlNode : whenSqlNodes) {
                if (sqlNode.apply(context)) {
                    return true;
                }
            }
        }
        if (defaultSqlNode != null) {
            return defaultSqlNode.apply(context);
        }
        return false;
    }
}
