package com.wangtao.dbhelper.reflection.invoker;

import com.wangtao.dbhelper.reflection.Reflector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangtao at 2018/12/26 10:45
 */
public class MethodInvoker implements Invoker {

    private Method method;

    public MethodInvoker(Method method) {
        this.method = method;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws InvocationTargetException, IllegalAccessException {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
                method.setAccessible(true);
                return method.invoke(target, args);
            }
            throw e;
        }
    }
}
