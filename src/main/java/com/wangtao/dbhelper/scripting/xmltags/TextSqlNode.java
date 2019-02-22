package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.parser.GenericTokenParser;
import com.wangtao.dbhelper.parser.TokenHandler;
import com.wangtao.dbhelper.scripting.OgnlCache;
import com.wangtao.dbhelper.type.SimpleTypeRegistry;

/**
 * 文本节点(动态, 包含${key}表达式的节点, key是运行时通过参数传进来的.)
 * 其它的key, 已经通过XNode getStringBody()方法解析成真正的值了.
 * @author wangtao
 * Created at 2019/1/22 15:00
 */
public class TextSqlNode implements SqlNode {

    private final String text;

    public TextSqlNode(String text) {
        this.text = text;
    }

    public boolean isDynamic() {
        DynamicCheckerTokenHandler tokenHandler = new DynamicCheckerTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("${", "}", tokenHandler);
        parser.parse(text);
        return tokenHandler.isDynamic;
    }

    public String getText() {
        return text;
    }

    @Override
    public void apply(DynamicContext context) {
        BindParameterTokenHandler tokenHandler = new BindParameterTokenHandler(context);
        GenericTokenParser parser = new GenericTokenParser("${", "}", tokenHandler);
        String sql = parser.parse(text);
        context.appendSql(sql);
    }

    private static class BindParameterTokenHandler implements TokenHandler {

        DynamicContext context;

        public BindParameterTokenHandler(DynamicContext context) {
            this.context = context;
        }

        @Override
        public String handleToken(String expression) {
            // 判断参数是不是简单类型, Ognl对简单类型无法导航(也不需要), 经过以下处理可以直接在XML中通过${value}获取值.
            Object parameter = context.getBindings().get(DynamicContext.PARAMETER);
            if(parameter == null) {
                context.getBindings().put("value", null);
            } else if(SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
                context.getBindings().put("value", parameter);
            }
            Object value = OgnlCache.getValue(context.getBindings(), expression);
            return value == null ? null : value.toString();
        }
    }

    private static class DynamicCheckerTokenHandler implements TokenHandler {

        private boolean isDynamic;

        @Override
        public String handleToken(String expression) {
            isDynamic = true;
            return null;
        }
    }
}
