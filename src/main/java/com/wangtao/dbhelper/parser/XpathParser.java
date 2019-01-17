package com.wangtao.dbhelper.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by wangtao at 2019/1/5 17:00
 */
public class XpathParser {

    /**
     * 文档对象
     */
    private final Document document;

    /**
     * 是否根据DTD文件校验XML文档
     */
    private boolean validating;

    /**
     * 用来定位XML文档内部引用的外部文件
     */
    private EntityResolver entityResolver;

    private XPath xpath;

    private Properties variables;

    private XpathParser(InputSource inputSource, boolean validating, EntityResolver entityResolver, Properties variables) {
        this(inputSource, validating, entityResolver);
        this.variables = variables;
    }

    private XpathParser(InputSource inputSource, boolean validating, EntityResolver entityResolver) {
        this.validating = validating;
        this.entityResolver = entityResolver;
        this.document = createDocument(inputSource);
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public String evalString(String expression) {
        return evalString(expression, document);
    }

    public String evalString(String expression, Object root) {
        return (String) evaluate(expression, root, XPathConstants.STRING);
    }

    public Boolean evalBoolean(String expression) {
        return evalBoolean(expression, document);
    }

    public Boolean evalBoolean(String expression, Object root) {
        return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
    }

    public Integer evalInteger(String expression) {
        return evalInteger(expression, document);
    }

    public Integer evalInteger(String expression, Object root) {
        return Integer.valueOf(evalString(expression, root));
    }

    public Double evalDouble(String expression) {
        return evalDouble(expression, document);
    }

    public Double evalDouble(String expression, Object root) {
        return Double.valueOf(evalString(expression, root));
    }

    public XNode evalNode(String expression) {
        return evalNode(expression, document);
    }

    public XNode evalNode(String expression, Object root) {
        Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
        return XNode.newXNode(node, this, variables);
    }

    public List<XNode> evalNodes(String expression) {
        return evalNodes(expression, document);
    }

    public List<XNode> evalNodes(String expression, Object root) {
        List<XNode> xnodes = new ArrayList<>();
        NodeList nodeList = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                xnodes.add(XNode.newXNode(nodeList.item(i), this, variables));
            }
        }
        return xnodes;
    }

    private Object evaluate(String expression, Object root, QName qName) {
        try {
            return xpath.evaluate(expression, root, qName);
        } catch (XPathExpressionException e) {
            throw new ParserException("解析XPath表达式出现错误, 原因: " + e);
        }
    }

    public Document getDocument() {
        return document;
    }

    public Element getRootElement() {
        return document.getDocumentElement();
    }

    private Document createDocument(InputSource inputSource) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 是否验证文档(根据DTD约束)
            factory.setValidating(validating);
            // 忽略元素之间的空白符(节点值空白符不会忽略)
            factory.setIgnoringElementContentWhitespace(true);
            // 忽略注释
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            // 如果开启验证, 需要设置错误处理器来输出错误.
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    // just ignore warning
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });
            return builder.parse(inputSource);
        } catch (Exception e) {
            throw new ParserException("创建文档对象发生错误, 原因:" + e);
        }
    }

    public static class Builder {

        private boolean validating;

        private EntityResolver entityResolver;

        private Reader reader;

        private String resource;

        private Properties variables;

        public Builder validating(boolean validating) {
            this.validating = validating;
            return this;
        }

        public Builder entityResolver(EntityResolver entityResolver) {
            this.entityResolver = entityResolver;
            return this;
        }

        public Builder reader(Reader reader) {
            this.reader = reader;
            return this;
        }

        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        public Builder variables(Properties variables) {
            this.variables = variables;
            return this;
        }

        public XpathParser build() {
            if (reader != null) {
                return new XpathParser(new InputSource(reader), validating, entityResolver, variables);
            } else if (resource != null) {
                return new XpathParser(new InputSource(resource), validating, entityResolver, variables);
            }
            throw new ParserException("请指定一个字符流或者资源位置");
        }
    }
}
