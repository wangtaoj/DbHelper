package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.core.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * @author wangtao
 * Created at 2019/2/22 15:30
 */
public class TrimSqlNode implements SqlNode {

    protected String prefix;

    protected String suffix;

    protected List<String> prefixOverrides;

    protected List<String> suffixOverrides;

    protected SqlNode contents;

    protected final Configuration configuration;

    public TrimSqlNode(Configuration configuration, SqlNode contents, String prefix,
                       String suffix, String prefixOverrides, String suffixOverrides) {
        this(configuration, contents, prefix, suffix,
                parseOverrides(prefixOverrides), parseOverrides(suffixOverrides));
    }

    public TrimSqlNode(Configuration configuration, SqlNode contents, String prefix,
                       String suffix, List<String> prefixOverrides, List<String> suffixOverrides) {
        this.configuration = configuration;
        this.prefix = prefix;
        this.suffix = suffix;
        this.prefixOverrides = prefixOverrides;
        this.suffixOverrides = suffixOverrides;
        this.contents = contents;
    }

    private static List<String> parseOverrides(String overrides) {
        List<String> results = new ArrayList<>();
        if (overrides != null) {
            StringTokenizer tokenizer = new StringTokenizer(overrides, "|");
            while (tokenizer.hasMoreTokens()) {
                results.add(tokenizer.nextToken().trim().toUpperCase(Locale.ENGLISH));
            }
        }
        return results;
    }

    @Override
    public boolean apply(DynamicContext context) {
        // 将trim元素内的SQL全部添加到filterDynamicContext中.
        FilterDynamicContext filterDynamicContext = new FilterDynamicContext(context);
        contents.apply(filterDynamicContext);

        // 处理prefix, suffix, prefixOverrides, suffixOverrides.
        String sql = filterDynamicContext.handleSql();
        if (sql.length() > 0) {
            context.appendSql(sql);
            return true;
        }
        return false;
    }

    /**
     * 临时上下文, 去掉SQL语句多余的部分, 添加prefix, suffix.
     */
    private class FilterDynamicContext extends DynamicContext {

        private DynamicContext delegate;

        private StringBuilder buffer;

        public FilterDynamicContext(DynamicContext delegate) {
            super(configuration, null);
            this.delegate = delegate;
            buffer = new StringBuilder();
        }

        public String handleSql() {
            StringBuilder sqlBuilder = new StringBuilder(getSql());
            if (sqlBuilder.length() > 0) {
                applyPrefix(sqlBuilder);
                applySuffix(sqlBuilder);
            }
            return sqlBuilder.toString();
        }

        private void applyPrefix(StringBuilder sqlBuilder) {
            String sql = sqlBuilder.toString();
            String upperSql = sql.toUpperCase(Locale.ENGLISH);
            if (prefixOverrides != null) {
                for (String toRemove : prefixOverrides) {
                    if (upperSql.startsWith(toRemove)) {
                        sqlBuilder.delete(0, toRemove.length());
                    }
                }
            }
            if (prefix != null) {
                sqlBuilder.insert(0, " ");
                sqlBuilder.insert(0, prefix);
            }
        }

        private void applySuffix(StringBuilder sqlBuilder) {
            String sql = sqlBuilder.toString();
            String upperSql = sql.toUpperCase(Locale.ENGLISH);
            if (suffixOverrides != null) {
                for (String toRemove : suffixOverrides) {
                    if (upperSql.endsWith(toRemove)) {
                        int end = sqlBuilder.length();
                        int start = end - toRemove.length();
                        sqlBuilder.delete(start, end);
                    }
                }
            }
            if (suffix != null) {
                sqlBuilder.append(" ");
                sqlBuilder.append(suffix);
            }
        }

        @Override
        public void appendSql(String sql) {
            buffer.append(sql);
            buffer.append(" ");
        }

        @Override
        public String getSql() {
            return buffer.toString().trim();
        }

        @Override
        public ContextMap getBindings() {
            return delegate.getBindings();
        }
    }
}
