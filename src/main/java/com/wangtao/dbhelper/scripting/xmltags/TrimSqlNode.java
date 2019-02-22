package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.core.Configuration;

import java.util.ArrayList;
import java.util.List;
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
            if (tokenizer.hasMoreTokens()) {
                results.add(tokenizer.nextToken().trim());
            }
        }
        return results;
    }

    @Override
    public void apply(DynamicContext context) {
        // 临时上下文, 去掉SQL语句多余的部分, 添加prefix, suffix.
        FilterDynamicContext filterDynamicContext = new FilterDynamicContext(context);
        contents.apply(filterDynamicContext);
    }

    private class FilterDynamicContext extends DynamicContext {

        private DynamicContext delegate;

        public FilterDynamicContext(DynamicContext delegate) {
            super(configuration, null);
            this.delegate = delegate;
        }

        public void applyAll() {
            StringBuilder buffer = new StringBuilder(getSql().trim());
            if (buffer.length() > 0) {
                applyPrefix(buffer);
                applySuffix(buffer);
                delegate.appendSql(buffer.toString());
            }
        }

        private void applyPrefix(StringBuilder buffer) {
            String sql = buffer.toString();
            if (prefixOverrides != null) {
                for (String toRemove : prefixOverrides) {
                    if (sql.startsWith(toRemove)) {
                        buffer.delete(0, toRemove.length());
                    }
                }
            }
            if (prefix != null) {
                buffer.insert(0, prefix);
            }
        }

        private void applySuffix(StringBuilder buffer) {
            String sql = buffer.toString();
            if (suffixOverrides != null) {
                for (String toRemove : suffixOverrides) {
                    if (sql.endsWith(toRemove)) {
                        int end = buffer.length();
                        int start = end - toRemove.length();
                        buffer.delete(start, end);
                    }
                }
            }
            if (suffix != null) {
                buffer.append(suffix);
            }
        }

        @Override
        public void appendSql(String sql) {
            super.appendSql(sql);
        }

        @Override
        public String getSql() {
            return super.getSql();
        }

        @Override
        public ContextMap getBindings() {
            return super.getBindings();
        }
    }
}
