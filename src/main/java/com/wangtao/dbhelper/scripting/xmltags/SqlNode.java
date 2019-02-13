package com.wangtao.dbhelper.scripting.xmltags;

/**
 * @author wangtao
 * Created at 2019/1/22 14:57
 */
public interface SqlNode {

    /**
     * context: 此对象用于获取最后的SQL语句
     * 解析SQL时会一个个地根据类型将SQL语句片段解析成自定义的SQL节点.
     * 最终会将SQL语句全部追加到context中
     * @param context DynamicContext实例
     */
    void apply(DynamicContext context);
}
