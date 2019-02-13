package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BaseBuilder;
import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.DynamicSqlSource;
import com.wangtao.dbhelper.mapping.RawSqlSource;
import com.wangtao.dbhelper.mapping.SqlSource;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.scripting.xmltags.MixedSqlNode;
import com.wangtao.dbhelper.scripting.xmltags.SqlNode;
import com.wangtao.dbhelper.scripting.xmltags.StaticTextSqlNode;
import com.wangtao.dbhelper.scripting.xmltags.TextSqlNode;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/22 14:19
 */
public class XMLScriptBuilder extends BaseBuilder {

    private XNode context;

    private boolean isDynamic;

    public XMLScriptBuilder(Configuration configuration, XNode context) {
        super(configuration);
        this.context = context;
    }

    public SqlSource createSqlSource() {
        MixedSqlNode rootSqlNode = parseDynamicTag(context);
        if(isDynamic) {
            return new DynamicSqlSource(configuration, rootSqlNode);
        } else {
            return new RawSqlSource(configuration, rootSqlNode);
        }
    }

    // 将SQL语句拆成一个个自定义节点.
    private MixedSqlNode parseDynamicTag(XNode node) {
        List<SqlNode> contents = new ArrayList<>();
        List<XNode> children = node.getChildren();
        for (XNode child : children) {
            short nodeType = child.getNode().getNodeType();
            if (nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE) {
                String body = child.getStringBody("");
                TextSqlNode textSqlNode = new TextSqlNode(body);
                if(textSqlNode.isDynamic()) {
                    isDynamic = true;
                    contents.add(textSqlNode);
                } else {
                    contents.add(new StaticTextSqlNode(body));
                }
            } else {
                throw new BuilderException("不知道的节点, 无法处理. 节点名称: " + child.getName());
            }
        }
        return new MixedSqlNode(contents);
    }
}
