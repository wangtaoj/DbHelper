package com.wangtao.dbhelper.reflection.invoker;

import com.wangtao.dbhelper.reflection.Reflector;

import java.lang.reflect.Field;

/**
 * Created by wangtao at 2018/12/26 10:57
 */
public class SetFieldInvoker implements Invoker {

    private Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException {
        try {
            field.set(target, args[0]);
        } catch (IllegalAccessException e) {
            if(Reflector.canControlMemberAccessible()) {
                field.setAccessible(true);
                field.set(target, args[0]);
            } else {
                throw e;
            }
        }
        return null;
    }
}
