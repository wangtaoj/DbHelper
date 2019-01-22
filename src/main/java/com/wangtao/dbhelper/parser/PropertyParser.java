package com.wangtao.dbhelper.parser;

import java.util.Properties;

/**
 * @author wangtao
 * Created at 2019/1/17 14:18
 */
public class PropertyParser {

    public static String parse(String expression, Properties variables) {
        VariableTokenHandler handler = new VariableTokenHandler(variables);
        GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
        return parser.parse(expression);
    }

    private static class VariableTokenHandler implements TokenHandler {

        private Properties variables;

        VariableTokenHandler(Properties variables) {
            this.variables = variables;
        }

        @Override
        public String handleToken(String expression) {
            if(variables != null) {
                if(variables.containsKey(expression)) {
                    return variables.getProperty(expression);
                }
            }
            // 没有变量值, 那么返回原来的值. 因为参数可能使用${}而不是#{}, 这样是动态表达式.
            return "${" + expression + "}";
        }
    }
}
