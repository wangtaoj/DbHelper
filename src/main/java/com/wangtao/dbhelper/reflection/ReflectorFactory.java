package com.wangtao.dbhelper.reflection;

/**
 * Created by wangtao at 2019/1/2 15:37
 */
public interface ReflectorFactory {

    Reflector findForClass(Class<?> clazz);
}
