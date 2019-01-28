package com.wangtao.dbhelper.reflection.invoker;

import com.wangtao.dbhelper.reflection.Reflector;

import java.lang.reflect.Field;

/**
 * @author wangtao
 * Create at 2018/12/26 11:01
 */
public class GetFieldInvoker implements Invoker {

    private Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            if(Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                return field.get(target);
            }
            throw e;
        }
    }
}
