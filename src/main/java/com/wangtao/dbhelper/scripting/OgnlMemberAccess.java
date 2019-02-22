package com.wangtao.dbhelper.scripting;

import com.wangtao.dbhelper.reflection.Reflector;
import ognl.MemberAccess;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.Map;

/**
 * 设置访问权限
 * @author wangtao
 * Created at 2019/1/23 17:18
 */
public class OgnlMemberAccess implements MemberAccess {

    private boolean canAccessMember;

    public OgnlMemberAccess() {
        this.canAccessMember = Reflector.canControlMemberAccessible();
    }

    @Override
    public Object setup(Map context, Object target, Member member, String propertyName) {
        Object result = null;
        if (isAccessible(context, target, member, propertyName)) {
            final AccessibleObject accessible = (AccessibleObject) member;
            if (!accessible.isAccessible()) {
                accessible.setAccessible(true);
                result = Boolean.FALSE;
            }
        }
        return result;
    }

    @Override
    public void restore(Map context, Object target, Member member, String propertyName, Object state) {
        if (state != null) {
            final AccessibleObject accessible = (AccessibleObject) member;
            accessible.setAccessible((Boolean) state);
        }
    }

    /**
     * 精确控制是否可访问权限.
     */
    @Override
    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        return canAccessMember;
    }
}
