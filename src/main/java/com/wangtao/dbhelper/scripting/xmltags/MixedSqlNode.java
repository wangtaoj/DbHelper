package com.wangtao.dbhelper.scripting.xmltags;

import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/22 14:58
 */
public class MixedSqlNode implements SqlNode {

    private List<SqlNode> contents;

    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
    public boolean apply(DynamicContext context) {
        if(contents != null) {
            for(SqlNode sqlNode : contents) {
                sqlNode.apply(context);
            }
        }
        return true;
    }
}
