package com.wangtao.dbhelper.parser;

/**
 * @author wangtao
 * Created at 2019/1/17 14:20
 */
public class GenericTokenParser {

    /**
     * 开始符
     */
    private String openToken;

    /**
     * 结束符
     */
    private String closeToken;

    /**
     * 处理表达式的Handler
     */
    private TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String parse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int openIndex = text.indexOf(openToken);
        if(openIndex == -1) {
            return text;
        }
        // 偏移量
        int offset = 0;
        StringBuilder result = new StringBuilder();
        while (openIndex != -1) {
            // 截取原字符串[offset, openIndex-1]内容
            result.append(text, offset, openIndex);
            offset = openIndex + openToken.length();
            // 寻找结尾标志
            int endIndex = text.indexOf(closeToken, offset);
            // 没有结尾标志
            if(endIndex == -1) {
                // 追加剩下所有内容
                result.append(text, openIndex, text.length());
                offset = text.length();
            } else {
                // 计算值
                String expression = text.substring(offset, endIndex);
                String newValue = handler.handleToken(expression);
                result.append(newValue);
                offset = endIndex + closeToken.length();
            }
            openIndex = text.indexOf(openToken, offset);
        }
        if(offset < text.length()) {
            result.append(text, offset, text.length());
        }
        return result.toString();
    }
}
