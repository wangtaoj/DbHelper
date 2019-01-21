package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BaseBuilder;
import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.builder.MapperBuilderAssistant;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.ResultMapping;
import com.wangtao.dbhelper.parser.DtdEntityResolver;
import com.wangtao.dbhelper.parser.XNode;
import com.wangtao.dbhelper.parser.XpathParser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangtao
 * Created at 2019/1/19 19:20
 */
public class XmlMapperBuilder extends BaseBuilder {

    private XpathParser parser;

    private String resource;

    private MapperBuilderAssistant assistant;

    public XmlMapperBuilder(Configuration configuration, Reader reader, String resource) {
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
        }
    }

    private void parseMapper(XNode mapper) {
        String namespace = mapper.getStringAttribute("namespace");
        if (namespace == null || namespace.equals("")) {
            throw new BuilderException(resource + " Mapper文件的namespace不能为空.");
        }
        assistant.setCurrentNamespace(namespace);
        resultMapElements(mapper.evalNodes("resultMap"));
        sqlElements(mapper.evalNodes("sql"));
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
        String type = resultMapNode.getStringAttribute("type",
                resultMapNode.getStringAttribute("javaType"));
        if(type == null || type.isEmpty()) {
            throw new BuilderException(resource + " Mapper文件定义的resultMap元素必须要一个type属性");
        }
        Class<?> typeClass = resolveClass(type);
        List<ResultMapping> resultMappings = new ArrayList<>();
        for(XNode child : resultMapNode.getChildren()) {
            ResultMapping resultMapping;
            if("id".equals(child.getName()) || "result".equals(child.getName())) {
                resultMapping = buildResultMappingByIdOrResult(child);
            } else {
                throw new BuilderException("resultMap元素只能有id或者result这两种子元素");
            }
            resultMappings.add(resultMapping);
        }
        assistant.addResultMap(id, typeClass, resultMappings);
    }

    private ResultMapping buildResultMappingByIdOrResult(XNode context) {
        String column = context.getStringAttribute("column");
        String property = context.getStringAttribute("property");
        if(column == null || column.isEmpty() || property == null || property.isEmpty()) {
            throw new BuilderException(resource + "中的" + context.getName() + "元素的column与property属性是必须的.");
        }
        return new ResultMapping.Builder().column(column).property(property).build();
    }

    private void sqlElements(List<XNode> sqlNodes) {
        for(XNode sqlNode : sqlNodes) {
            String id = sqlNode.getStringAttribute("id");
            if(id == null || id.isEmpty()) {
                throw new BuilderException("位于" + resource + "文件中的sql元素节点需要一个id属性");
            }
            id = assistant.applyNamespace(id);
            configuration.getSqlNodes().put(id, sqlNode);
        }
    }
}
