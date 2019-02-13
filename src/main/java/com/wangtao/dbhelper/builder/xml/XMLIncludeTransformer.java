package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.builder.MapperBuilderAssistant;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.parser.PropertyParser;
import com.wangtao.dbhelper.parser.XNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Properties;

/**
 * @author wangtao
 * Created at 2019/1/22 11:19
 */
public class XMLIncludeTransformer {

    private Configuration configuration;

    private MapperBuilderAssistant assistant;

    public XMLIncludeTransformer(Configuration configuration, MapperBuilderAssistant assistant) {
        this.configuration = configuration;
        this.assistant = assistant;
    }

    public void parseInclude(Node source) {
        parseInclude(source, configuration.getVariables(), false);
    }

    /**
     * 解析<include>节点, 并且用其refid引用的<sql>节点内容替换<include>.
     * 对于sql节点以及后代节点的${}表达式全都替换成真正的值.
     * @param source 节点
     * @param variables 变量集合
     * @param included 控制是否替换${}表达式
     */
    private void parseInclude(Node source, Properties variables, boolean included) {
        if (source.getNodeName().equals("include")) {
            Node sqlNode = findSqlNode(getStringAttribute(source, "refid"), variables);
            // 递归调用解析<sql>节点, 并替换其中的${}
            parseInclude(sqlNode, variables, true);
            // 用<sql>替换<include>
            source.getParentNode().replaceChild(sqlNode, source);
            // 将<sql>后代节点插到<sql>节点前
            while (sqlNode.hasChildNodes()) {
                sqlNode.getParentNode().insertBefore(sqlNode.getFirstChild(), sqlNode);
            }
            // 移除<sql>
            sqlNode.getParentNode().removeChild(sqlNode);
        } else if (source.getNodeType() == Node.ELEMENT_NODE) {
            /*
             * 首次调用时, 节点为<select><update><delete><insert>,
             * 只有<sql>节点以及子节点需要替换${}表达式.
             */
            if (included && !variables.isEmpty()) {
                NamedNodeMap attributes = source.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);
                    attr.setNodeValue(PropertyParser.parse(attr.getNodeValue(), variables));
                }
            }
            NodeList children = source.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                // included跟随上一次的值
                Node child = children.item(i);
                parseInclude(child, variables, included);
            }
        } else if (included && source.getNodeType() == Node.TEXT_NODE && !variables.isEmpty()) {
            String newNodeValue = PropertyParser.parse(source.getNodeValue(), variables);
            source.setNodeValue(newNodeValue);
        }
    }


    private Node findSqlNode(String refid, Properties variables) {
        refid = PropertyParser.parse(refid, variables);
        refid = assistant.applyNamespace(refid);
        XNode sqlNode = configuration.getSqlNodes().get(refid);
        if (sqlNode == null) {
            throw new BuilderException("引用的sql片段不存在, refid = " + refid);
        }
        return sqlNode.getNode().cloneNode(true);
    }

    private String getStringAttribute(Node node, String name) {
        return node.getAttributes().getNamedItem(name).getNodeValue();
    }
}
