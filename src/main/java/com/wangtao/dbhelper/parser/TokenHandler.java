package com.wangtao.dbhelper.parser;

/**
 * @author wangtao
 * Created at 2019/1/17 13:56
 */
public interface TokenHandler {

    /**
     * 根据传入参数获取新的值
     * @param expression 参数
     * @return 返回运算后的值
     */
    String handleToken(String expression);
}
