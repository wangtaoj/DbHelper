package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BaseBuilder;
import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.DynamicSqlSource;
import com.wangtao.dbhelper.mapping.RawSqlSource;
import com.wangtao.dbhelper.mapping.SqlSource;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.scripting.xmltags.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/22 14:19
 */
public class XMLScriptBuilder extends BaseBuilder {

    private XNode context;

    private boolean isDynamic;

    private Map<String, NodeHandler> nodeHandlerMap = new HashMap<>();

    public XMLScriptBuilder(Configuration configuration, XNode context) {
        super(configuration);
        this.context = context;
        nodeHandlerMap.put("trim", new TrimHandler());
        nodeHandlerMap.put("if", new IfHandler());
        nodeHandlerMap.put("where", new WhereHandler());
        nodeHandlerMap.put("set", new SetHandler());
        nodeHandlerMap.put("choose", new ChooseHandler());
        nodeHandlerMap.put("when", new IfHandler());
        nodeHandlerMap.put("otherwise", new OtherwiseHandler());
        nodeHandlerMap.put("foreach", new ForeachHandler());
    }

    public SqlSource createSqlSource() {
        MixedSqlNode rootSqlNode = parseDynamicTag(context);
        if (isDynamic) {
            return new DynamicSqlSource(configuration, rootSqlNode);
        } else {
            return new RawSqlSource(configuration, rootSqlNode);
        }
    }

    // 将SQL语句拆成一个个自定义节点.
    private MixedSqlNode parseDynamicTag(XNode node) {
        List<SqlNode> contents = new ArrayList<>();
        NodeList children = node.getNode().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            XNode child = node.newXNode(children.item(i));
            short nodeType = child.getNode().getNodeType();
            if (nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE) {
                String body = child.getStringBody("");
                TextSqlNode textSqlNode = new TextSqlNode(body);
                if (textSqlNode.isDynamic()) {
                    isDynamic = true;
                    contents.add(textSqlNode);
                } else {
                    contents.add(new StaticTextSqlNode(body));
                }
            } else if (nodeType == Node.ELEMENT_NODE) {
                NodeHandler nodeHandler = nodeHandlerMap.get(child.getName());
                if (nodeHandler == null) {
                    throw new BuilderException("Unknown element<" + child.getName() + "> in SQL statement.");
                }
                isDynamic = true;
                nodeHandler.handleNode(child, contents);
            }
        }
        return new MixedSqlNode(contents);
    }

    /**
     * 动态节点处理器
     * @author wangtao
     * Created at 2019/2/22 14:19
     */
    private interface NodeHandler {

        /**
         * 解析动态节点(<trim>, <if>, <foreach>, <where>, <set>, <choose>, <when>, <otherwise>)
         * @param dynamicNode 动态节点(上述节点)
         * @param contents    容器, 用来保存动态节点解析的结果.
         */
        void handleNode(XNode dynamicNode, List<SqlNode> contents);
    }

    /**
     * 处理<trim>动态节点
     */
    private class TrimHandler implements NodeHandler {
        @Override
        public void handleNode(XNode dynamicNode, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTag(dynamicNode);
            String prefix = dynamicNode.getStringAttribute("prefix");
            String suffix = dynamicNode.getStringAttribute("suffix");
            String prefixOverrides = dynamicNode.getStringAttribute("prefixOverrides");
            String suffixOverrides = dynamicNode.getStringAttribute("suffixOverrides");
            TrimSqlNode sqlNode = new TrimSqlNode(configuration, mixedSqlNode, prefix, suffix,
                    prefixOverrides, suffixOverrides);
            contents.add(sqlNode);
        }
    }

    /**
     * 处理<if>动态节点
     */
    private class IfHandler implements NodeHandler {
        @Override
        public void handleNode(XNode dynamicNode, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTag(dynamicNode);
            String test = dynamicNode.getStringAttribute("test");
            IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
            contents.add(ifSqlNode);
        }
    }

    private class WhereHandler implements NodeHandler {
        @Override
        public void handleNode(XNode dynamicNode, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTag(dynamicNode);
            WhereSqlNode whereSqlNode = new WhereSqlNode(configuration, mixedSqlNode);
            contents.add(whereSqlNode);
        }
    }

    private class SetHandler implements NodeHandler {
        @Override
        public void handleNode(XNode dynamicNode, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTag(dynamicNode);
            SetSqlNode setSqlNode = new SetSqlNode(configuration, mixedSqlNode);
            contents.add(setSqlNode);
        }
    }

    private class ChooseHandler implements NodeHandler {
        @Override
        public void handleNode(XNode dynamicNode, List<SqlNode> contents) {
            List<SqlNode> whenSqlNodes = new ArrayList<>();
            List<SqlNode> defaultSqlNodes = new ArrayList<>();
            List<XNode> children = dynamicNode.getChildren();
            for (XNode child : children) {
                String nodeName = child.getName();
                NodeHandler nodeHandler = nodeHandlerMap.get(nodeName);
                if (nodeHandler instanceof IfHandler) {
                    nodeHandler.handleNode(child, whenSqlNodes);
                } else if (nodeHandler instanceof OtherwiseHandler) {
                    nodeHandler.handleNode(child, defaultSqlNodes);
                }
            }
            SqlNode defaultSqlNde = getDefaultSqlNode(defaultSqlNodes);
            ChooseSqlNode chooseSqlNode = new ChooseSqlNode(whenSqlNodes, defaultSqlNde);
            contents.add(chooseSqlNode);
        }

        private SqlNode getDefaultSqlNode(List<SqlNode> defaultSqlNodes) {
            if (defaultSqlNodes.size() == 1) {
                return defaultSqlNodes.get(0);
            } else if (defaultSqlNodes.size() >= 2) {
                throw new BuilderException("Too many otherwise element in choose statement.");
            }
            return null;
        }
    }

    private class OtherwiseHandler implements NodeHandler {
        @Override
        public void handleNode(XNode dynamicNode, List<SqlNode> contents) {
            MixedSqlNode mixedSqlNode = parseDynamicTag(dynamicNode);
            contents.add(mixedSqlNode);
        }
    }

    private class ForeachHandler implements NodeHandler {
        @Override
        public void handleNode(XNode dynamicNode, List<SqlNode> contents) {

        }
    }

}
