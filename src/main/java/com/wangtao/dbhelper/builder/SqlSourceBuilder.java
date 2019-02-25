package com.wangtao.dbhelper.builder;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.mapping.ParameterMapping;
import com.wangtao.dbhelper.mapping.SqlSource;
import com.wangtao.dbhelper.mapping.StaticSqlSource;
import com.wangtao.dbhelper.parser.GenericTokenParser;
import com.wangtao.dbhelper.parser.TokenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 主要完成两方面的操作.
 * 1. 解析#{}表达式, 将其中的内容转换成ParameterMapping对象
 * 2. 将#{}替换成?占位符
 * @author wangtao
 * Created at 2019/1/23 9:32
 */
public class SqlSourceBuilder extends BaseBuilder {

    private static final String parameterProperties = "jdbcType|typeHandler";

    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    public SqlSource parse(String originalSql) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql);
        return new StaticSqlSource(configuration, sql, handler.parameterMappings);
    }

    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        private List<ParameterMapping> parameterMappings = new ArrayList<>();

        public ParameterMappingTokenHandler(Configuration configuration) {
            super(configuration);
        }

        @Override
        public String handleToken(String expression) {
            parameterMappings.add(buildParamterMapping(expression));
            return "?";
        }

        private ParameterMapping buildParamterMapping(String expression) {
            Map<String, String> map = parseParamterMapping(expression);
            String property = map.get("property");
            ParameterMapping.Builder builder = new ParameterMapping.Builder(property);
            for(Map.Entry<String, String> entry : map.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (Objects.equals("property", name)) {
                    // just ignore
                } else if(Objects.equals("jdbcType", name)) {
                    builder.jdbcType(resolveJdbcType(value));
                } else if(Objects.equals("typeHandler", name)) {
                    builder.typeHandler(resolveTypeHandler(value));
                } else {
                    throw new BuilderException("解析#{" + expression + "}发生错误." + name + "是无效的属性, 请参考" + parameterProperties);
                }
            }
            return builder.build();
        }

        private Map<String, String> parseParamterMapping(String expression) {
            try {
                return new ParameterExpression(expression).parse();
            } catch (BuilderException e) {
                throw e;
            } catch (Exception e) {
                throw new BuilderException("解析#{" + expression + "}出现出错. 原因:" + e);
            }
        }
    }
 }
