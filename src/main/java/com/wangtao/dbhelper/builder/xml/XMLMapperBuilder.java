package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BaseBuilder;
import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.builder.MapperBuilderAssistant;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.ResultMapping;
import com.wangtao.dbhelper.parser.DtdEntityResolver;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.parser.XpathParser;
import com.wangtao.dbhelper.reflection.MetaClass;
import com.wangtao.dbhelper.type.JdbcType;
import com.wangtao.dbhelper.type.TypeHandler;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/19 19:20
 */
public class XMLMapperBuilder extends BaseBuilder {

    private XpathParser parser;

    private String resource;

    private MapperBuilderAssistant assistant;

    public XMLMapperBuilder(Configuration configuration, Reader reader, String resource) {
        super(configuration);
        this.resource = resource;
        this.assistant = new MapperBuilderAssistant(configuration, resource);
        this.parser = new XpathParser.Builder()
                .reader(reader).validating(true)
                .entityResolver(new DtdEntityResolver())
                .build();
    }

    public void parse() {
        // 未解析过
        if (!configuration.isLoadedResource(resource)) {
            parseMapper(parser.evalNode("/mapper"));
            configuration.addLoadedResource(resource);
        }
    }

    private void parseMapper(XNode mapper) {
        try {
            String namespace = mapper.getStringAttribute("namespace");
            if (namespace == null || namespace.equals("")) {
                throw new BuilderException(resource + " Mapper文件的namespace不能为空.");
            }
            assistant.setCurrentNamespace(namespace);
            // 解析<resultMap>
            resultMapElements(mapper.evalNodes("resultMap"));
            // 解析<sql>
            sqlElements(mapper.evalNodes("sql"));
            // 解析<select> <update> <insert> <delete>
            buildStatementFromContext(mapper.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("错误的解析Mapper文件, 文件位置位于" + resource + ".", e);
        }

    }

    private void resultMapElements(List<XNode> resultMapNodes) {
        for (XNode resultMapNode : resultMapNodes) {
            resultMapElement(resultMapNode);
        }
    }

    private void resultMapElement(XNode resultMapNode) {
        // id属性
        String id = resultMapNode.getStringAttribute("id",
                resultMapNode.getValueBasedIdentifier());
        // type属性
        String type = resultMapNode.getStringAttribute("type");
        if (type == null || type.isEmpty()) {
            throw new BuilderException(resource + " Mapper文件定义的resultMap元素必须要一个type属性");
        }
        Class<?> typeClass = resolveClass(type);
        List<ResultMapping> resultMappings = new ArrayList<>();
        for (XNode child : resultMapNode.getChildren()) {
            ResultMapping resultMapping;
            if ("id".equals(child.getName()) || "result".equals(child.getName())) {
                resultMapping = buildResultMappingByIdOrResult(child, typeClass);
            } else {
                throw new BuilderException("resultMap元素只能有id或者result这两种子元素");
            }
            resultMappings.add(resultMapping);
        }
        assistant.addResultMap(id, typeClass, resultMappings);
    }

    private ResultMapping buildResultMappingByIdOrResult(XNode context, Class<?> resultType) {
        String column = context.getStringAttribute("column");
        String property = context.getStringAttribute("property");
        String javaTypeString = context.getStringAttribute("javaType");
        Class<?> javaType = resolveJavaType(javaTypeString, property, resultType);
        String jdbcTypeString = context.getStringAttribute("jdbcType");
        JdbcType jdbcType = resolveJdbcType(jdbcTypeString);
        TypeHandler<?> typeHandler = getTypeHandler(javaType, jdbcType);
        if (column == null || column.isEmpty() || property == null || property.isEmpty()) {
            throw new BuilderException(resource + "中的" + context.getName() + "元素的column与property属性是必须的.");
        }
        return new ResultMapping.Builder(column, property)
                .jdbcType(jdbcType).javaType(javaType)
                .typeHandler(typeHandler)
                .build();
    }

    private Class<?> resolveJavaType(String javaTypeString, String property, Class<?> resultType) {
        Class<?> javaType = resolveClass(javaTypeString);
        if(javaType == null) {
            try {
                MetaClass metaClass = MetaClass.forClass(resultType);
                // resultType = map, 会报错.
                javaType = metaClass.getSetterType(property);
            } catch (Exception e) {
                javaType = null;
            }
        }
        return javaType == null ? Object.class : javaType;
    }

    private void sqlElements(List<XNode> sqlNodes) {
        for (XNode sqlNode : sqlNodes) {
            String id = sqlNode.getStringAttribute("id");
            if (id == null || id.isEmpty()) {
                throw new BuilderException("位于" + resource + "文件中的sql元素节点需要一个id属性");
            }
            id = assistant.applyNamespace(id);
            configuration.getSqlNodes().put(id, sqlNode);
        }
    }

    private void buildStatementFromContext(List<XNode> xNodes) {
        for (XNode context : xNodes) {
            buildStatementFromContext(context);
        }
    }

    private void buildStatementFromContext(XNode context) {
        XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, assistant, context);
        statementParser.parseStatementNode();
    }
}
