package com.wangtao.dbhelper.mapping;

/**
 * @author wangtao
 * Created at 2019/1/19 19:07
 */
public class ResultMapping {

    private String column;

    private String property;

    ResultMapping() {

    }

    public String getColumn() {
        return column;
    }

    public String getProperty() {
        return property;
    }

    public static class Builder {
        private ResultMapping resultMapping = new ResultMapping();

        public Builder column(String column) {
            resultMapping.column = column;
            return this;
        }

        public Builder property(String property) {
            resultMapping.property = property;
            return this;
        }

        public ResultMapping build() {
            return resultMapping;
        }
    }
}
