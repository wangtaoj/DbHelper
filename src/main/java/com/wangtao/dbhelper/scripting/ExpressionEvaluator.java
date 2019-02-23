package com.wangtao.dbhelper.scripting;

import com.wangtao.dbhelper.builder.BuilderException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        } else if (value instanceof String) {
            return !((String) value).isEmpty();
        } else if (value instanceof Character) {
            return (Character) value != '0';
        } else {
            return value != null;
        }
    }

    /**
     * 获取表达式的值.
     * @param root       根对象
     * @param expression Ognl表达式
     * @return Iterable对象
     */
    public static Iterable<?> evaluateIterable(Object root, String expression) {
        Object value = OgnlCache.getValue(root, expression);
        if (value == null) {
            throw new BuilderException("The value of expression '" + expression +
                    "' equals null. We need a iterable object!");
        }
        if (value instanceof Iterable<?>) {
            return (Iterable<?>) value;
        }
        if (value.getClass().isArray()) {
            List<Object> results = new ArrayList<>();
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                results.add(Array.get(value, i));
            }
            return results;
        }
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).entrySet();
        }
        throw new BuilderException("Error evaluate the expression '" + expression + "'."
                + "The value '" + value + "' was not iterable.");
    }
}
