package com.wangtao.dbhelper.reflection;

import com.wangtao.dbhelper.reflection.invoker.GetFieldInvoker;
import com.wangtao.dbhelper.reflection.invoker.Invoker;
import com.wangtao.dbhelper.reflection.invoker.MethodInvoker;
import com.wangtao.dbhelper.reflection.invoker.SetFieldInvoker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.util.*;

/**
 * Created by wangtao at 2018/12/26 9:38
 */
public class Reflector {

    /**
     * 类型
     */
    private final Class<?> type;

    /**
     * setter方法集合, key: 属性名, value: Invoker对象, 调用method.invoke | field.set() 设置属性值
     */
    private final Map<String, Invoker> setMethods = new HashMap<>();

    /**
     * getter方法集合, key: 属性名, value: Invoker对象, 调用method.invoke | field.get() 设置属性值
     */
    private final Map<String, Invoker> getMethods = new HashMap<>();

    /**
     * 属性集合, 忽略大小写
     * key: 属性名大写, value: 属性名
     */
    private final Map<String, String> caseInsensitiveMap = new HashMap<>();

    /**
     * set方法参数类型集合
     */
    private final Map<String, Class<?>> setParameterTypes = new HashMap<>();

    /**
     * get方法参数类型集合
     */
    private final Map<String, Class<?>> getReturnTypes = new HashMap<>();

    public Reflector(Class<?> clazz) {
        this.type = clazz;
        addGetMethods(clazz);
        addSetMethods(clazz);
        addFields(clazz);
        String[] readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        String[] writeablePropertyName = setMethods.keySet().toArray(new String[0]);
        for (String propName : readablePropertyNames) {
            caseInsensitiveMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
        for (String propName : writeablePropertyName) {
            caseInsensitiveMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }

    private void addGetMethods(Class<?> clazz) {
        Map<String, List<Method>> conflictMethods = new HashMap<>();
        Method[] allMethods = getAllMethods(clazz);
        for (Method method : allMethods) {
            if (validateGetMethodName(method)) {
                String propertyName = getPropertyNameFromMethod(method.getName());
                List<Method> methods = conflictMethods.computeIfAbsent(propertyName, key -> new ArrayList<>());
                methods.add(method);
            }

        }
        // 解决冲突, 可能存在多个方法处理后得到同样的属性名
        resolveConflictGetMethods(conflictMethods);
    }

    private void addGetMethod(String propName, Method method) {
        if (!Objects.equals("serialVersionUID", propName)) {
            getMethods.put(propName, new MethodInvoker(method));
            getReturnTypes.put(propName, method.getReturnType());
        }
    }

    private void resolveConflictGetMethods(Map<String, List<Method>> conflictMethods) {
        for (Map.Entry<String, List<Method>> entry : conflictMethods.entrySet()) {
            List<Method> methods = entry.getValue();
            String propertyName = entry.getKey();
            Method winner = null;
            for (Method candidate : methods) {
                if (winner == null) {
                    winner = candidate;
                    continue;
                }
                Class<?> winnerReturnType = winner.getReturnType();
                Class<?> candidateReturnType = candidate.getReturnType();
                if (winnerReturnType == candidateReturnType) {
                    // Boolean类型的getter方法是get开头不是is
                    if (candidateReturnType == boolean.class) {
                        if (candidate.getName().startsWith("is")) {
                            winner = candidate;
                        }
                    } else {
                        throw new ReflectionException("不合法的覆盖getter方法, 破坏了Java Bean规范," +
                                "该类中拥有两个getter方法, 返回值模棱两可, 不确定性. 具体类:" + type.getName() +
                                "具体方法: " + candidate.getName() + ", " + winner.getName());
                    }
                    //candidateReturnType == boolean.class || candidateReturnType == Boolean.class
                } else if (winnerReturnType.isAssignableFrom(candidateReturnType)) {
                    // 候选者返回值更具体, 是子类
                    winner = candidate;
                } else if (candidateReturnType.isAssignableFrom(winnerReturnType)) {
                    // yes, just do nothing
                } else {
                    throw new ReflectionException("不合法的覆盖getter方法, 破坏了Java Bean规范," +
                            "该类中拥有两个getter方法, 返回值模棱两可, 不确定性. 具体类:" + type.getName() +
                            "具体方法: " + candidate.getName() + ", " + winner.getName());
                }
            }
            addGetMethod(propertyName, winner);
        }
    }

    private void addSetMethods(Class<?> clazz) {
        Map<String, List<Method>> conflictMethods = new HashMap<>();
        Method[] allMethods = getAllMethods(clazz);
        for (Method method : allMethods) {
            if (validateSetMethodName(method)) {
                String propertyName = getPropertyNameFromMethod(method.getName());
                List<Method> methods = conflictMethods.computeIfAbsent(propertyName, key -> new ArrayList<>());
                methods.add(method);
            }
        }
        resolveConflictsetMethods(conflictMethods);
    }

    private void resolveConflictsetMethods(Map<String, List<Method>> conflictMethods) {
        for (Map.Entry<String, List<Method>> entry : conflictMethods.entrySet()) {
            List<Method> setters = entry.getValue();
            String propertyName = entry.getKey();
            Method winner = null;
            for (Method candidate : setters) {
                if (winner == null) {
                    winner = candidate;
                    continue;
                }
                Class<?> winnerParameterType = winner.getParameterTypes()[0];
                Class<?> candidateParameterType = candidate.getParameterTypes()[0];
                if (winnerParameterType.isAssignableFrom(candidateParameterType)) {
                    winner = candidate;
                } else if (candidateParameterType.isAssignableFrom(winnerParameterType)) {
                    // just do nothing
                } else {
                    throw new ReflectionException("不合法的setter方法定义, 破坏了Java Bean规范," +
                            "该类中拥有两个setter方法. 具体类:" + type.getName() +
                            "具体方法: " + candidate.getName() + ", " + winner.getName());
                }
            }
            addSetMethod(propertyName, winner);
        }
    }

    private void addSetMethod(String propName, Method setter) {
        if (!Objects.equals("serialVersionUID", propName)) {
            setMethods.put(propName, new MethodInvoker(setter));
            setParameterTypes.put(propName, setter.getParameterTypes()[0]);
        }
    }

    private void addFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!setMethods.containsKey(field.getName())) {
                int modifiers = field.getModifiers();
                // 对于static final共同修饰的常量, 反射是不能修改值的
                if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers))) {
                    addSetField(field);
                }
            }
            if (!getMethods.containsKey(field.getName())) {
                addGetField(field);
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            addFields(superClass);
        }
    }

    private void addSetField(Field field) {
        setMethods.put(field.getName(), new SetFieldInvoker(field));
        setParameterTypes.put(field.getName(), field.getType());
    }

    private void addGetField(Field field) {
        getMethods.put(field.getName(), new GetFieldInvoker(field));
        getReturnTypes.put(field.getName(), field.getType());
    }

    /**
     * 获取指定类中所有的方法, 包括从父类继承的不可见私有方法.
     * @return 方法列表
     */
    public static Method[] getAllMethods(Class<?> clazz) {
        Map<String, Method> uniqueMethds = new HashMap<>();
        while (clazz != null && clazz != Object.class) {
            Method[] methods = clazz.getDeclaredMethods();
            addUniqueMethod(uniqueMethds, methods);
            // 可能是抽象类, 未实现抽象方法, 因此需要处理接口
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> superinterface : interfaces) {
                // 接口中方法默认public, 直接调用getMethods方法即可
                addUniqueMethod(uniqueMethds, superinterface.getMethods());
            }
            clazz = clazz.getSuperclass();
        }
        return uniqueMethds.values().toArray(new Method[0]);
    }

    /**
     * 桥接方法: 通常发生于继承与泛型同在的场景
     * 例如:
     * class abstract A<T> {
     * public abstract T getName(T t);
     * }
     * class SubB extends A<String> {
     * public String getName(String t) {
     * return "11";
     * }
     * }
     * 因为泛型擦除的原因, 泛型擦除后A中方法定义实际上是public abstract Object getName(Object t)
     * 为了保持字节码继承语义, 编译器自动给SubB也添加了一个public Object getName(Object t)方法,
     * 称之为桥接方法, 因此实际上SubB中存在两个getName方法, 一个是编译器生成的桥接方法, 一个是声明的方法.
     * 对于桥接方法, 我们并不需要它, 只要找到实际声明的方法即可.
     */
    private static void addUniqueMethod(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method method : methods) {
            // 非桥接方法
            if (!method.isBridge()) {
                String signature = getMethodSignature(method);
                uniqueMethods.putIfAbsent(signature, method);
            }
        }
    }

    /**
     * 获取方法签名
     * 形如:
     * java.lang.String#getName(int, java.lang.String)
     * void#getName(int, java.lang.Integer)
     * @param method 方法
     * @return 方法签名
     */
    public static String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        if (method.getReturnType() != null) {
            sb.append(method.getReturnType().getName()).append("#");
        }
        sb.append(method.getName()).append('(');
        int i = 0;
        for (Class<?> clazz : method.getParameterTypes()) {
            if (i == 0) {
                sb.append(clazz.getName());
            } else {
                sb.append(',').append(clazz.getName());
            }
            i++;
        }
        return sb.append(')').toString();
    }

    /**
     * 是否可以控制成员的访问性质
     * method.setAccessible()方法前置判断条件
     * @return true/false
     */
    public static boolean canControlMemberAccessible() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

    /**
     * 校验方法是不是一个getter方法
     * getXXXX, isXXXX
     * @param method 方法
     * @return 是getter方法 ? true : false
     */
    public static boolean validateGetMethodName(Method method) {
        String methodName = method.getName();
        boolean flag = methodName != null && methodName.startsWith("get") && methodName.length() > 3;
        flag = flag || (methodName != null && methodName.startsWith("is") && methodName.length() > 2);
        if (flag) {
            return method.getParameterCount() == 0;
        }
        return false;
    }

    /**
     * 校验方法是不是一个setter方法
     * setXXXX
     * @param method 方法
     * @return 是setter方法 ? true : false
     */
    public static boolean validateSetMethodName(Method method) {
        String methodName = method.getName();
        boolean flag = methodName != null && methodName.startsWith("set") && methodName.length() > 3;
        if (flag) {
            return method.getParameterCount() == 1;
        }
        return false;
    }

    /**
     * 从getter/setter方法获取属性名字
     * 如果不是getter/setter方法, return null
     * @param methodName 方法名字
     * @return 属性名字
     */
    public static String getPropertyNameFromMethod(String methodName) {
        if (methodName.startsWith("is")) {
            return getPropertyNameFromMethod1(methodName, 2);
        } else if (methodName.startsWith("get")) {
            return getPropertyNameFromMethod1(methodName, 3);
        } else if (methodName.startsWith("set")) {
            return getPropertyNameFromMethod1(methodName, 3);
        }
        throw new ReflectionException("不是JavaBean标准的getter or setter方法");
    }

    private static String getPropertyNameFromMethod1(String methodName, int length) {
        String temp = null;
        int afterLen = length + 1;
        if (methodName.length() > afterLen) {
            temp = methodName.substring(afterLen);
        }
        String firstLetter = methodName.substring(length, afterLen);
        return temp == null ? firstLetter.toLowerCase() : firstLetter.toLowerCase() + temp;
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * 查找真正的属性名
     * @param propName 不区分大小写的属性名称
     * @return 属性名
     */
    public String findPropName(String propName) {
        return caseInsensitiveMap.get(propName.toUpperCase(Locale.ENGLISH));
    }

    /**
     * 是否有setter方法
     * @param propName 属性名
     * @return 存在setter方法 ? true : false
     */
    public boolean hasSetter(String propName) {
        return setMethods.containsKey(propName);
    }

    /**
     * 是否有getter方法
     * @param propName 属性名
     * @return 存在getter方法 ? true : false
     */
    public boolean hasGetter(String propName) {
        return getMethods.containsKey(propName);
    }

    /**
     * 获取Invoker, 用来设置属性值
     * @param propName 属性名
     * @return 对应的Invoker
     */
    public Invoker getSetInvoker(String propName) {
        Invoker invoker = setMethods.get(propName);
        if (invoker == null) {
            throw new ReflectionException(String.format("在类%s中没有对应的Setter与属性名: %s匹配",
                    type.getName(), propName));
        }
        return invoker;
    }

    /**
     * 获取Invoker, 用来获取属性值
     * @param propName 属性名
     * @return 对应的Invoker
     */
    public Invoker getGetInvoker(String propName) {
        Invoker invoker = getMethods.get(propName);
        if (invoker == null) {
            throw new ReflectionException(String.format("在类%s中没有对应的Getter与属性名: %s匹配",
                    type.getName(), propName));
        }
        return invoker;
    }

    /**
     * 获取setter参数类型
     * @param propName 属性名
     * @return 参数类型
     */
    public Class<?> getSetParameterType(String propName) {
        Class<?> paramterType = setParameterTypes.get(propName);
        if (paramterType == null) {
            throw new ReflectionException(String.format("在类%s中没有对应的Setter与属性名: %s匹配",
                    type.getName(), propName));
        }
        return paramterType;
    }

    /**
     * 获取getter返回类型
     * @param propName 属性名
     * @return 返回值类型
     */
    public Class<?> getGetReturnType(String propName) {
        Class<?> returnType = getReturnTypes.get(propName);
        if (returnType == null) {
            throw new ReflectionException(String.format("在类%s中没有对应的Getter与属性名: %s匹配",
                    type.getName(), propName));
        }
        return returnType;
    }
}
