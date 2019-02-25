package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.core.Configuration;
import com.wangtao.dbhelper.parser.GenericTokenParser;
import com.wangtao.dbhelper.scripting.ExpressionEvaluator;

import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/2/22 22:27
 */
public class ForeachSqlNode implements SqlNode {

    public static final String ADDITIONAL_PARAMETER_PREFIX = "__frch_";

    private final Configuration configuration;

    private SqlNode contents;

    private String collection;

    private String item;

    private String index;

    private String open;

    private String close;

    private String separator;

    public ForeachSqlNode(Configuration configuration, SqlNode contents, String collection, String item,
                          String index, String open, String close, String separator) {
        this.configuration = configuration;
        this.contents = contents;
        this.collection = collection;
        this.item = item == null ? "item" : item;
        this.index = index == null ? "index" : index;
        this.open = open;
        this.close = close;
        this.separator = separator;
    }

    @Override
    public boolean apply(DynamicContext context) {
        Iterable<?> iterable = ExpressionEvaluator.evaluateIterable(context.getBindings(), collection);
        if (!iterable.iterator().hasNext()) {
            return false;
        }
        int i = 0;
        applyOpen(context);
        for (Object element : iterable) {
            // 插入分隔串
            if (i > 0) {
                applySeparator(context);
            }
            if (element instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) element;
                bindItem(context, entry.getValue(), i);
                bindIndex(context, entry.getKey(), i);
            } else {
                bindItem(context, element, i);
                bindIndex(context, i, i);
            }
            contents.apply(new ForeachContentDynamicContext(configuration, context, i));
            i++;
        }
        applyClose(context);
        // 去除, 绑定原来名称的目的是因为${}表达式取值, SQL构建完之后并不需要, 因此移除.
        context.getBindings().remove(item);
        context.getBindings().remove(index);
        return true;
    }

    private void bindItem(DynamicContext context, Object value, int order) {
        // 绑定原来的key, foreach中可能存在${}取值, 因此需要绑定原来的.
        context.bind(item, value);
        // 绑定新生成的key
        context.bind(appendOrderForKey(item, order), value);
    }

    private void bindIndex(DynamicContext context, Object value, int order) {
        context.bind(index, value);
        context.bind(appendOrderForKey(index, order), value);
    }

    /**
     * 生成额外参数名称.
     */
    private String appendOrderForKey(String key, int order) {
        return ADDITIONAL_PARAMETER_PREFIX + key + "_" + order;
    }

    private void applyOpen(DynamicContext context) {
        if (open != null) {
            context.appendSql(open);
        }
    }

    private void applySeparator(DynamicContext context) {
        if (separator != null) {
            context.appendSql(separator);
        }
    }

    private void applyClose(DynamicContext context) {
        if (close != null) {
            context.appendSql(close);
        }
    }

    class ForeachContentDynamicContext extends DynamicContext {

        DynamicContext delegate;

        int order;

        public ForeachContentDynamicContext(Configuration configuration, DynamicContext delegate, int order) {
            super(configuration, null);
            this.delegate = delegate;
            this.order = order;
        }

        @Override
        public void bind(String name, Object value) {
            delegate.bind(name, value);
        }

        @Override
        public void appendSql(String sql) {
            GenericTokenParser parser = new GenericTokenParser("#{", "}", expression -> {
                if (!expression.isEmpty()) {
                    /*
                     * 正则表达式含义: 以item(前面可以有空格)字符串开头, item右侧第一个字符必须满足要么是空格, 逗号, 点或者没有字符.
                     * 举例:
                     * expression = item1  false
                     * expression = item   true  -> item
                     * expression = item.age  true  -> item(点不会包含进来)
                     * expression = item,  true  -> item(逗号不会包含进来)
                     */
                    expression = expression.replaceFirst("^\\s*" + item + "(?=[\\s,.]?)", appendOrderForKey(item, order));
                    expression = expression.replaceFirst("^\\s*" + index + "(?=[\\s,.]?)", appendOrderForKey(index, order));
                }
                return "#{" + expression + "}";
            });
            delegate.appendSql(parser.parse(sql));
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        @Override
        public ContextMap getBindings() {
            return delegate.getBindings();
        }
    }
}
