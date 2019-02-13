package com.wangtao.dbhelper.scripting.xmltags;

/**
 * @author wangtao
 * Created at 2019/1/22 15:13
 */
public class StaticTextSqlNode implements SqlNode {

    private final String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(text);
    }
}
