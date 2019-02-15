package com.wangtao.dbhelper.mapping;

import java.util.*;

/**
 * @author wangtao
 * Created at 2019/1/19 19:13
 */
public class ResultMap {

    private String id;

    private Class<?> type;

    private List<ResultMapping> resultMappings = new ArrayList<>();
    private Set<String> mappedColumns = new HashSet<>();
    private Set<String> mappedProperties = new HashSet<>();

    ResultMap() {

    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public Set<String> getMappedColumns() {
        return mappedColumns;
    }

    public Set<String> getMappedProperties() {
        return mappedProperties;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public static class Builder {

        private ResultMap resultMap = new ResultMap();

        public Builder(String id, Class<?> type) {
            resultMap.id = id;
            resultMap.type = type;
        }

        public Builder resultMappings(List<ResultMapping> resultMappings) {
            resultMap.resultMappings = resultMappings;
            return this;
        }

        public ResultMap build() {
            for(ResultMapping resultMapping : resultMap.resultMappings) {
                resultMap.mappedColumns.add(resultMapping.getColumn());
                resultMap.mappedProperties.add(resultMapping.getProperty());
            }
            resultMap.resultMappings = Collections.unmodifiableList(resultMap.resultMappings);
            resultMap.mappedProperties = Collections.unmodifiableSet(resultMap.mappedProperties);
            resultMap.mappedColumns = Collections.unmodifiableSet(resultMap.mappedColumns);
            return resultMap;
        }
    }
}
