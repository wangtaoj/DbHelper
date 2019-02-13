package com.wangtao.dbhelper.builder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangtao
 * Created at 2019/1/23 11:15
 */
public class ParameterExpression {

    private Map<String, String> map = new HashMap<>();

    private final String expression;

    public ParameterExpression(String expression) {
        this.expression = expression;
    }

    public Map<String, String> parse() {
        property();
        return map;
    }

    private void property() {
        int left = skipWhitespace(0);
        if (left < expression.length()) {
            int right = skipUntil(left, ",");
            String property = trim(left, right);
            if (!property.isEmpty()) {
                map.put("property", property);
                if(right < expression.length()) {
                    option(right + 1);
                }
                return;
            }
        }
        throw new BuilderException("解析#{" + expression + "}出现错误, 请检查#{}表达式语法, 属性值为空.");
    }

    private void option(int start) {
        int left = skipWhitespace(start);
        if(left < expression.length()) {
            int right = skipUntil(left, "=");
            String name = trim(left, right);
            left = right + 1;
            right = skipUntil(left, ",");
            String value = trim(left, right);
            map.put(name, value);
            option(right + 1);
        }
    }

    /**
     * 跳过空白符.
     * @param start 开始索引
     * @return 首个非空白符的索引
     */
    private int skipWhitespace(int start) {
        for (int i = start; i < expression.length(); i++) {
            if (expression.charAt(i) > 32) {
                return i;
            }
        }
        return expression.length();
    }

    /**
     * @param left 开始索引
     * @param end 终止字符串.
     * @return 返回终止字符串的位置.
     */
    private int skipUntil(int left, String end) {
        int right = expression.indexOf(end, left);
        return right != -1 ? right : expression.length();
    }

    private String trim(int left, int right) {
        return expression.substring(left, right).trim();
    }
}
