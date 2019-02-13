package com.wangtao.dbhelper.scripting.xmltags;

import com.wangtao.dbhelper.builder.BuilderException;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangtao
 * Created at 2019/1/23 16:10
 */
public class OgnlCache {

    private static Map<String, Object> expressionCache = new ConcurrentHashMap<>();

    private static final MemberAccess MEMBER_ACCESS = new OgnlMemberAccess();

    /**
     * 使用ognl表达式取值
     * 注: 如果访问的成员不存在或者没有权限访问时抛出ognl.NoSuchPropertyException
     * 权限访问通过MemberAccess接口设置.
     * @param root 根对象
     * @param expression ognl表达式
     * @return 根据表达式从根对象返回的值
     * @throws BuilderException 取值时发生任意错误抛出异常.
     */
    public static Object getValue(Object root, String expression) {
        try {
            Map context = Ognl.createDefaultContext(root, MEMBER_ACCESS);
            return Ognl.getValue(parseExpression(expression), context, root);
        } catch (OgnlException e) {
            throw new BuilderException("根据表达式'" + expression + "'获取值时发生错误", e);
        }
    }

    private static Object parseExpression(String expression) throws OgnlException {
        Object cache = expressionCache.get(expression);
        if(cache == null) {
            cache = Ognl.parseExpression(expression);
            expressionCache.put(expression, cache);
        }
        return cache;
    }
}
