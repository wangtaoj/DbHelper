package com.wangtao.dbhelper.scripting;

import java.math.BigDecimal;

/**
 * @author wangtao
 * Created at 2019/2/22 15:40
 */
public class ExpressionEvaluator {

    /**
     * 获取表达式的boolean值.
     * @param root       根对象
     * @param expression Ognl表达式
     * @return true/false
     */
    public static boolean evaluateBoolean(Object root, String expression) {
        Object value = OgnlCache.getValue(root, expression);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return new BigDecimal(String.valueOf(value)).compareTo(BigDecimal.ZERO) != 0;
        } else {
            return value != null;
        }
    }
}
