package com.wangtao.dbhelper.mapping;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.executor.keygen.KeyGenerator;
import com.wangtao.dbhelper.logging.Log;
import com.wangtao.dbhelper.logging.LogFactory;

import java.util.Arrays;

/**
 * @author wangtao
 * Created at 2019/1/22 10:35
 */
public class MappedStatement {

    private String id;

    private final Configuration configuration;

    private ResultMap resultMap;

    private SqlSource sqlSource;

    private SqlCommandType sqlCommandType;

    private StatementType statementType;

    private Integer fetchSize;

    private Integer timeout;

    private ResultSetType resultSetType;

    private String[] keyPropertys;

    private String[] keyColumns;

    private KeyGenerator keyGenerator;

    private Log statementLog;

    MappedStatement(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ResultMap getResultMap() {
        return resultMap;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public ResultSetType getResultSetType() {
        return resultSetType;
    }

    public String[] getKeyPropertys() {
        return keyPropertys;
    }

    public String[] getKeyColumns() {
        return keyColumns;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public Log getStatementLog() {
        return statementLog;
    }

    public static class Builder {
        private MappedStatement mappedStatement;

        public Builder(Configuration configuration, String id, SqlSource sqlSource) {
            this.mappedStatement = new MappedStatement(configuration);
            mappedStatement.id = id;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.statementLog = LogFactory.getLogger(id);
        }

        public Builder resultMap(ResultMap resultMap) {
            mappedStatement.resultMap = resultMap;
            return this;
        }

        public Builder sqlCommandType(SqlCommandType sqlCommandType) {
            mappedStatement.sqlCommandType = sqlCommandType;
            return this;
        }

        public Builder statementType(StatementType statementType) {
            mappedStatement.statementType = statementType;
            return this;
        }

        public Builder fetchSize(Integer fetchSize) {
            mappedStatement.fetchSize = fetchSize;
            return this;
        }

        public Builder timeout(Integer timeout) {
            mappedStatement.timeout = timeout;
            return this;
        }

        public Builder resultSetType(ResultSetType resultSetType) {
            mappedStatement.resultSetType = resultSetType;
            return this;
        }

        public Builder keyProperty(String keyProperty) {
            mappedStatement.keyPropertys = convertStringToArray(keyProperty);
            return this;
        }

        public Builder keyColumn(String keyColumn) {
            mappedStatement.keyColumns = convertStringToArray(keyColumn);
            return this;
        }

        public Builder keyGenerator(KeyGenerator keyGenerator) {
            mappedStatement.keyGenerator = keyGenerator;
            return this;
        }

        private static String[] convertStringToArray(String content) {
            if(content == null || content.trim().isEmpty()) {
                return new String[]{};
            }
            return Arrays.stream(content.split(",")).map(String::trim).toArray(String[]::new);
        }

        public MappedStatement build() {
            return mappedStatement;
        }
    }
}
