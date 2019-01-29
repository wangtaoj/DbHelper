package com.wangtao.dbhelper.reflection;

import java.lang.reflect.*;

/**
 * @author wangtao
 * Created at 2019/1/29 10:42
 */
public class TypeParameterResolver {

    private TypeParameterResolver() {

    }

    /**
     * 解析字段的运行时类型.
     * @param field    待检测字段
     * @param srcClass 要检测的类
     * @return 返回字段运行时类型
     */
    public static Type resolveFieldType(Field field, Class<?> srcClass) {
        Type fieldType = field.getGenericType();
        return resolveType(fieldType, srcClass, field.getDeclaringClass());
    }

    /**
     * 解析方法的运行时返回值类型.
     * @param method   待检测方法
     * @param srcClass 要检测的类
     * @return 方法的运行时返回值类型
     */
    public static Type resolveReturnType(Method method, Class<?> srcClass) {
        Type returnType = method.getGenericReturnType();
        return resolveType(returnType, srcClass, method.getDeclaringClass());
    }

    /**
     * 解析方法中所有参数的运行时类型.
     * @param method   待检测方法
     * @param srcClass 要检测的类
     * @return 所有参数的运行时类型数组
     */
    public static Type[] resolveParamTypes(Method method, Class<?> srcClass) {
        Type[] result = new Type[method.getParameterCount()];
        Class<?> declaringClass = method.getDeclaringClass();
        for (int i = 0; i < result.length; i++) {
            Type paramType = method.getGenericParameterTypes()[i];
            result[i] = resolveType(paramType, srcClass, declaringClass);
        }
        return result;
    }

    /**
     * 解析方法中指定位置参数的运行时类型.
     * @param method   待检测方法
     * @param index    参数位置, 从0开始.
     * @param srcClass 要检测的类
     * @return 指定位置参数的运行时类型
     */
    public static Type resolveParamType(Method method, int index, Class<?> srcClass) {
        if (index >= 0 && index < method.getParameterCount()) {
            Type paramType = method.getGenericParameterTypes()[index];
            return resolveType(paramType, srcClass, method.getDeclaringClass());
        } else {
            throw new IllegalArgumentException("参数索引超出方法参数个数, index = " + index + "方法参数个数 = "
                    + method.getParameterCount() + ".");
        }
    }

    /**
     * 通过srcType以及declaringClass这两个辅助参数将type参数解析成真正的运行时类型.
     * 其中:
     * type:    字段的类型或者方法参数类型, 返回值类型.
     * srcType: 解析的起点类, 此参数用Type类型而不是用Class, 因为需要获取泛型信息.
     * declaringClass: 声明此字段或者方法的类.
     * declaringClass与srcType的关系
     * 1) declaringClass 与 srcType所指示的原始类型相等
     * 2) declaringClass 是 srcType所指示的原始类型的基类
     * @param type           待解析的类型
     * @param srcType        当前类型
     * @param declaringClass 声明类
     * @return 返回解析之后的类型
     */
    private static Type resolveType(Type type, Type srcType, Class<?> declaringClass) {
        if (type instanceof TypeVariable<?>) {
            return resolveTypeVariable((TypeVariable<?>) type, srcType, declaringClass);
        } else if (type instanceof ParameterizedType) {
            return resolveParameterizedType((ParameterizedType) type, srcType, declaringClass);
        } else if (type instanceof GenericArrayType) {
            return resolveGenericArrayType((GenericArrayType) type, srcType, declaringClass);
        } else {
            // 本身是原始类型或者基本类型, 直接返回即可.
            return type;
        }
    }

    private static Type resolveParameterizedType(ParameterizedType type, Type srcType, Class<?> declaringClass) {
        // 获取实际类型参数, 用来解析
        Type[] typeArgs = type.getActualTypeArguments();
        Type[] newTypeArgs = new Type[typeArgs.length];

        /*
         * 开始解析, 实际类型参数只有4种
         * 1) 原始类型(Class).  List<String> -> 实际类型参数是String
         * 2) 参数化类型(ParameterizedType).  List<List<String>>  ->  实际类型参数是List<String>
         * 3) 类型变量(TypeVariable).  List<T>  ->  实际类型参数是T
         * 4) 通配符(WildcardType).    List<? extends Number>  -> 实际类型参数是 ? extends Number
         */
        for (int i = 0; i < typeArgs.length; i++) {
            if (typeArgs[i] instanceof ParameterizedType) {
                newTypeArgs[i] = resolveParameterizedType((ParameterizedType) typeArgs[i], srcType, declaringClass);
            } else if (typeArgs[i] instanceof TypeVariable) {
                newTypeArgs[i] = resolveTypeVariable((TypeVariable<?>) typeArgs[i], srcType, declaringClass);
            } else if (typeArgs[i] instanceof WildcardType) {
                newTypeArgs[i] = resolveWildcardType((WildcardType) typeArgs[i], srcType, declaringClass);
            } else {
                newTypeArgs[i] = typeArgs[i];
            }
        }
        return new ParameterizedTypeImpl(newTypeArgs, type.getRawType(), type.getOwnerType());
    }

    private static Type resolveWildcardType(WildcardType type, Type srcType, Class<?> declaringClass) {
        Type[] lowBounds = resolveWildCardType(type.getLowerBounds(), srcType, declaringClass);
        Type[] upperBounds = resolveWildCardType(type.getUpperBounds(), srcType, declaringClass);
        return new WildcardTypeImpl(upperBounds, lowBounds);
    }

    private static Type[] resolveWildCardType(Type[] bounds, Type srcType, Class<?> declaringClass) {
        Type[] newBounds = new Type[bounds.length];
        /*
         * 边界类型:
         * 1) 参数化类型.   ? extends List<String>.
         * 2) 类型变量.     ? extends T.
         * 3) 原始类型.     ? extends List.
         */
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i] instanceof ParameterizedType) {
                newBounds[i] = resolveParameterizedType((ParameterizedType) bounds[i], srcType, declaringClass);
            } else if (bounds[i] instanceof TypeVariable<?>) {
                newBounds[i] = resolveTypeVariable((TypeVariable<?>) bounds[i], srcType, declaringClass);
            } else {
                newBounds[i] = bounds[i];
            }
        }
        return newBounds;
    }

    private static Type resolveGenericArrayType(GenericArrayType type, Type srcType, Class<?> declaringClass) {
        Type componentType = type.getGenericComponentType();
        Type newComponentType = null;
        // component type 要么是一个TypeVariable要么是一个ParameterizedType类型.
        if (componentType instanceof TypeVariable<?>) {
            newComponentType = resolveTypeVariable((TypeVariable<?>) componentType, srcType, declaringClass);
        } else if (componentType instanceof ParameterizedType) {
            newComponentType = resolveParameterizedType((ParameterizedType) componentType, srcType, declaringClass);
        } else if (componentType instanceof GenericArrayType) {
            // 多维数组的情况, component type会是GenericArrayType类型
            newComponentType = resolveGenericArrayType((GenericArrayType) componentType, srcType, declaringClass);
        }
        // 类型变量被解析成了原始类型, 那么就成了普通数组了.
        if (newComponentType instanceof Class<?>) {
            return Array.newInstance((Class<?>) newComponentType, 0).getClass();
        }
        return new GenericArrayTypeImpl(newComponentType);
    }

    private static Type resolveTypeVariable(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass) {
        // 获取当前类的原始类型
        Class<?> srcClass;
        if (srcType instanceof Class<?>) {
            srcClass = (Class<?>) srcType;
        } else if (srcType instanceof ParameterizedType) {
            srcClass = (Class<?>) ((ParameterizedType) srcType).getRawType();
        } else {
            throw new IllegalArgumentException("第二个参数必须是一个Class或者ParameterizedType类型, 实际是"
                    + srcType.getClass());
        }
        // 当前类就是声明类, 返回类型变量的上边界, 没有声明上边界便是Object.
        if (srcClass == declaringClass) {
            return typeVar.getBounds()[0];
        }

        // 获取父类型, 继续解析, 希望借助父类型(带有泛型信息)从而得到此类型变量的运行时类型.
        Type superClassType = srcClass.getGenericSuperclass();
        Type result = scanSuperTypes(typeVar, superClassType, srcType, declaringClass);
        if (result != null) {
            return result;
        }

        // 扫描所有父接口
        Type[] superInterfaces = srcClass.getGenericInterfaces();
        for (Type superInterfaceType : superInterfaces) {
            result = scanSuperTypes(typeVar, superInterfaceType, srcType, declaringClass);
            if (result != null) {
                return result;
            }
        }
        /*
         * 经过以上步骤依然没有找到运行时类型, 返回此类型变量的上边界.
         * 比如方法中定义的类型变量, 借助父类型是得不到正确的运行时类型, 因为父类没有该类型变量的声明信息.
         */
        return typeVar.getBounds()[0];
    }

    private static Type scanSuperTypes(TypeVariable<?> typeVar, Type superClassType, Type srcType, Class<?> declaringClass) {
        // 声明类型变量的地方只能是方法或者类, 对于声明类型变量的类来说其类型肯定属于参数化类型.
        if (superClassType instanceof ParameterizedType) {
            ParameterizedType parentAsType = (ParameterizedType) superClassType;
            Class<?> parentAsClass = (Class<?>) ((ParameterizedType) superClassType).getRawType();
            /*
             * 1. 如果当前类型是参数化类型
             *    1.1 参数传的是原生类型, 如果当前类型的父类型中实际运行参数中存在类型变量,
             *        那么依然是类型变量, 无法根据子类(当前类型)来确定此类型变量的实际运行类型.
             *    1.2 参数传的是参数化类型, 那么可以利用当前类型的实际运行参数将父类型中类型变量替换掉.
             *    举例:
             *    当前类型srcType = ArrayList, 则parentAsType = List<T>, 无法确定T.
             *    当前类型srcClass = ArrayList<Integer>, 则parentAsType = List<T>, 可确定T = Integer.
             * 2. 如果当前类型是原生类型, 那么其父类型(带泛型信息)中声明的类型变量已经是真实参数了.
             *
             * class A<T> {
             *     T item;
             * }
             * class B<T> extends A<T> {}
             * class C extends B<String> {}
             * 第一次调用:
             * typeVar = T, declaringClass = A.class;
             * srcType = C.class,  srcClass = C.class;
             * parentAsType = srcClass.getGenericSuperclass() -> B<String>;
             * parentAsClass = B.class;
             * 因为 declaringClass != parentAsClass
             * 第二次调用:
             * typeVar = T, declaringClass = A.class;
             * srcType = B<String>, srcClass = B.class;
             * parentAsType = srcClass.getGenericSuperclass() -> A<T>;
             * parentAsClass = A.class;
             * 当前类型srcType是参数化类型, 需要替换parentAsType中的类型变量T -> String
             */
            if (srcType instanceof ParameterizedType) {
                parentAsType = replaceTypeVarOfParent((ParameterizedType) srcType, parentAsType);
            }

            if (parentAsClass == declaringClass) {
                // 获取实际参数类型
                Type[] typeArgs = parentAsType.getActualTypeArguments();
                // 获取类型变量
                TypeVariable<?>[] typeVars = parentAsClass.getTypeParameters();
                for (int i = 0; i < typeVars.length; i++) {
                    if (typeVars[i] == typeVar) {
                        return typeArgs[i] instanceof TypeVariable<?> ?
                                ((TypeVariable<?>) typeArgs[i]).getBounds()[0] : typeArgs[i];
                    }
                }
            } else if (declaringClass.isAssignableFrom(parentAsClass)) {
                // 继续向上递归解析
                return resolveTypeVariable(typeVar, parentAsType, declaringClass);
            }
        } else if (superClassType instanceof Class<?> && declaringClass.isAssignableFrom((Class<?>) superClassType)) {
            // 父类型是原始类型, 继续往上递归解析.
            return resolveTypeVariable(typeVar, superClassType, declaringClass);
        }
        return null;
    }

    /**
     * 将父类型(参数化类型)的实际类型参数里的类型变量替换成实际类型.
     * Example:
     * 基于以下定义:
     * <pre>{@code
     *      class First<K, V> {}
     *      class Second<V> extends First<Integer, V> {}
     * }</pre>
     * 参数如下所示:
     * <pre>{@code
     *      srcType = Second<String>;
     *      parentType = First<Integer, V>;
     * }</pre>
     * 方法作用就是将parentType中的类型变量V替换成实际类型String.
     * 因为Second<String> -> First<Integer, String>.
     * 即First中声明的两个类型变量K -> Integer, V -> String.
     * @param srcType    当前类型, 带泛型信息
     * @param parentType 当前类型的父类型, 带泛型信息
     * @return 父类型实际类型参数里的类型变量替换后的结果.
     */
    private static ParameterizedType replaceTypeVarOfParent(ParameterizedType srcType, ParameterizedType parentType) {
        // 获取父类实际参数类型       ->   [Integer.class, V]
        Type[] parentTypeArgs = parentType.getActualTypeArguments();
        // 获取当前类实际参数类型     ->   [String.Class]
        Type[] srcTypeArgs = srcType.getActualTypeArguments();
        // 获取当前类原始类型         ->   Second.class
        Class<?> srcClass = (Class<?>) srcType.getRawType();
        // 获取当前类声明的类型变量   ->    [V]
        TypeVariable<?>[] srctTypeVars = srcClass.getTypeParameters();
        Type[] newParentArgs = new Type[parentTypeArgs.length];
        boolean isChange = false;
        for (int i = 0; i < parentTypeArgs.length; i++) {
            if (parentTypeArgs[i] instanceof TypeVariable<?>) {
                for (int j = 0; j < srctTypeVars.length; j++) {
                    if (parentTypeArgs[i] == srctTypeVars[j]) {
                        newParentArgs[i] = srcTypeArgs[j];
                        isChange = true;
                        break;
                    }
                }
            } else {
                newParentArgs[i] = parentTypeArgs[i];
            }
        }
        return isChange ? new ParameterizedTypeImpl(newParentArgs,
                parentType.getRawType(), parentType.getOwnerType()) : parentType;
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {

        Type[] actualTypeArguments;

        Type rawType;

        Type ownerType;

        public ParameterizedTypeImpl(Type[] actualTypeArguments, Type rawType, Type ownerType) {
            super();
            this.actualTypeArguments = actualTypeArguments;
            this.rawType = rawType;
            this.ownerType = ownerType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }
    }

    private static class WildcardTypeImpl implements WildcardType {

        Type[] upperBounds;

        Type[] lowerBounds;

        public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
            super();
            this.upperBounds = upperBounds;
            this.lowerBounds = lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds;
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds;
        }
    }

    private static class GenericArrayTypeImpl implements GenericArrayType {

        Type componentType;

        public GenericArrayTypeImpl(Type componentType) {
            super();
            this.componentType = componentType;
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }
    }
}
