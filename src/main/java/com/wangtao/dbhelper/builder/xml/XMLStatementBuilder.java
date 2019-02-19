package com.wangtao.dbhelper.builder.xml;

import com.wangtao.dbhelper.builder.BaseBuilder;
import com.wangtao.dbhelper.builder.BuilderException;
import com.wangtao.dbhelper.builder.MapperBuilderAssistant;
import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.*;
import com.wangtao.dbhelper.parser.XNode;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author wangtao
 * Created at 2019/1/22 11:06
 */
public class XMLStatementBuilder extends BaseBuilder {

    private XNode context;

    private MapperBuilderAssistant assistant;

    private String resource;

    public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant assistant, XNode context) {
        super(configuration);
        this.context = context;
        this.assistant = assistant;
        this.resource = assistant.getResource();
    }

    public void parseStatementNode() {
        String id = context.getStringAttribute("id");
        if(id == null || id.isEmpty()) {
            throw new BuilderException("位于" + resource + "文件中的" + context.getName() + "元素节点需要一个id属性");
        }
        id = assistant.applyNamespace(id);
        String resultMap = context.getStringAttribute("resultMap");
        String resultType = context.getStringAttribute("resultType");
        Class<?> resultTypeClass = resolveClass(resultType);

        String nodeName = context.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

        StatementType statementType = StatementType.valueOf(context.getStringAttribute("statementType",
                StatementType.PREPARED.name()));
        Integer fetchSize = context.getIntegerAttribute("fetchSize");
        Integer timeout = context.getIntegerAttribute("timeout");
        ResultSetType resultSetType = ResultSetType.valueOf(context.getStringAttribute("resultSetType",
                ResultSetType.DEFAULT.name()));

        String keyProperty = context.getStringAttribute("keyProperty");
        String keyColumn = context.getStringAttribute("keyColumn");
        boolean useGeneratedKeys = context.getBooleanAttribute("useGeneratedKeys", false);
        KeyGenerator keyGenerator = useGeneratedKeys ? JDBCKeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;

        // 解析<include>元素, 并移除.
        XMLIncludeTransformer includeTransformer = new XMLIncludeTransformer(configuration, assistant);
        includeTransformer.parseInclude(context.getNode());

        // 解析SQL, 处理#, $占位符以及动态节点.
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, context);
        SqlSource sqlSource = xmlScriptBuilder.createSqlSource();
        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, id, sqlSource)
                .resultMap(getResultMap(id, resultMap, resultTypeClass)).sqlCommandType(sqlCommandType)
                .statementType(statementType).fetchSize(fetchSize)
                .timeout(timeout).resultSetType(resultSetType)
                .keyProperty(keyProperty).keyColumn(keyColumn)
                .keyGenerator(keyGenerator).build();
        configuration.addMappedStatement(mappedStatement);
    }

    private ResultMap getResultMap(String statementId, String resultMapId, Class<?> resultType) {
        if(resultMapId != null) {
            resultMapId = assistant.applyNamespace(resultMapId);
            return configuration.getResultMap(resultMapId);
        } else if(resultType != null) {
            return new ResultMap.Builder(statementId + "-Inline", resultType)
                    .resultMappings(new ArrayList<>())
                    .build();
        }
        return null;
    }
}
