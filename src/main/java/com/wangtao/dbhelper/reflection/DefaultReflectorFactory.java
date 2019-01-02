package com.wangtao.dbhelper.reflection;

/**
 * Created by wangtao at 2019/1/2 15:38
 */
public class DefaultReflectorFactory implements ReflectorFactory {

    @Override
    public Reflector findForClass(Class<?> clazz) {
        return new Reflector(clazz);
    }
}
