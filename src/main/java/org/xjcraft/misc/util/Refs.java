package org.xjcraft.misc.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class Refs {
    // - Bukkit
    /** CraftBukkit 的版本 */
    public static final String BUKKIT_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    /**
     * 根据路径获取一个 CraftBukkit 包里的 Class
     *
     * @param path 在版本号之后的路径, 如获取 org.bukkit.craftbukkit.v1_9_R1.CraftServer 时应该传入 CraftServer
     * @return 对应类的 Class
     */
    public static Class<?> cbClass(String path) throws Exception {
        // 拼凑完整 Path
        path = String.format("org.bukkit.craftbukkit.%s.%s", Refs.BUKKIT_VERSION, path);
        // 获取并返回 Class
        return Class.forName(path);
    }

    // - 搜索
    /**
     * 搜索属性
     *
     * @param clazz 属性所在的 Class，会从这个 Class 一级一级往上查，直到 Object(不含)
     * @param fieldName 属性名
     * @return 找到的属性，如果没找到则返回 null
     */
    // TODO 这个写法其实并不会搜索接口
    public static Field searchField(Class<?> clazz, String fieldName) throws Exception {
        Field field;
        while (true) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
                if (clazz == Object.class || clazz == null) {
                    throw e;
                }
            }
        }
    }

    /**
     * 搜索方法
     *
     * @param clazz 方法所在的 Class，会从这个 Class 一级一级往上查，直到 Object(不含)
     * @param methodName 方法名
     * @param argTypes 参数的类型列表
     * @return 找到的方法，如果没找到则返回 null
     */
    public static Method searchMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) throws Exception {
        // 查找方法
        Method method;
        while (true) {
            try {
                method = clazz.getDeclaredMethod(methodName, argTypes);
                return method;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
                if (clazz == Object.class || clazz == null) {
                    throw e;
                }
            }
        }
    }

    // - 属性
    /**
     * 获取一个属性的值
     *
     * @param clazz 属性所在的 Class，会从这个 Class 一级一级往上查，直到 Object(不含)
     * @param object 属性所在的对象, 如果是静态属性, 则忽略此参数
     * @param fieldName 属性名
     * @return 该属性的值
     */
    public static Object getFieldValue(Class<?> clazz, Object object, String fieldName) throws Exception {
        // 查找属性
        Field field = Refs.searchField(clazz, fieldName);
        field.trySetAccessible();

        // 获取值并返回结果
        return field.get(object);
    }

    /**
     * 获取一个属性的值
     *
     * @param object 属性所在的对象
     * @param fieldName 属性名
     * @return 该属性的值
     */
    public static Object getFieldValue(Object object, String fieldName) throws Exception {
        return Refs.getFieldValue(object.getClass(), object, fieldName);
    }

    /**
     * 设置一个属性的值
     *
     * @param clazz 属性所在的 Class，会从这个 Class 一级一级往上查，直到 Object(不含)
     * @param object 属性所在的对象, 如果是静态属性, 则忽略此参数
     * @param fieldName 属性名
     * @param value 目标值
     */
    public static void setFieldValue(Class<?> clazz, Object object, String fieldName, Object value) throws Exception {
        // 查找属性
        Field field = Refs.searchField(clazz, fieldName);
        field.trySetAccessible();

        // 设置值
        field.set(object, value);
    }

    /**
     * 设置一个属性的值
     *
     * @param object 属性所在的对象
     * @param fieldName 属性名
     * @param value 目标值
     */
    public static void setFieldValue(Object object, String fieldName, Object value) throws Exception {
        Refs.setFieldValue(object.getClass(), object, fieldName, value);
    }

    // - 方法
    /**
     * 调用一个方法
     *
     * @param clazz 方法所在的 Class，会从这个 Class 一级一级往上查，直到 Object(不含)
     * @param object 方法所在的对象, 如果是静态方法, 则忽略此参数
     * @param methodName 方法名
     * @param args 参数列表
     * @param argTypes 参数的类型列表
     * @return 方法的返回值
     */
    public static Object invokeMethodLimitArgTypes(Class<?> clazz, Object object, String methodName, Object[] args, Class<?>[] argTypes) throws Exception {
        // 查找方法
        Method method = Refs.searchMethod(clazz, methodName, argTypes);
        method.trySetAccessible();

        // 调用方法并返回结果
        return method.invoke(object, args);
    }

    /**
     * 调用一个方法
     *
     * @param clazz 方法所在的 Class，会从这个 Class 一级一级往上查，直到 Object(不含)
     * @param object 方法所在的对象, 如果是静态方法, 则忽略此参数
     * @param methodName 方法名
     * @param args 参数列表
     * @return 方法的返回值
     */
    public static Object invokeMethod(Class<?> clazz, Object object, String methodName, Object... args) throws Exception {
        Class<?>[] parameterTypes = new Class<?>[args.length];
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }
        return Refs.invokeMethodLimitArgTypes(clazz, object, methodName, args, parameterTypes);
    }

    /**
     * 调用一个方法
     *
     * @param object 方法所在的对象
     * @param methodName 方法名
     * @param args 参数列表
     * @return 方法的返回值
     */
    public static Object invokeMethod(Object object, String methodName, Object... args) throws Exception {
        return Refs.invokeMethod(object.getClass(), object, methodName, args);
    }

    // - 构造方法
    /**
     * 调用一个构造函数来实例化一个对象
     *
     * @param clazz 要被实例化的 Class
     * @param args 参数列表
     * @param argTypes 参数的类型列表
     * @return 实例化后的对象
     */
    public static Object invokeConstructorLimitArgTypes(Class<?> clazz, Object[] args, Class<?>[] argTypes) throws Exception {
        // 查找构造函数
        Constructor<?> constructor = clazz.getDeclaredConstructor(argTypes);
        constructor.trySetAccessible();

        // 调用构造函数并返回结果
        return constructor.newInstance(args);
    }

    /**
     * 调用一个构造函数来实例化一个对象
     *
     * @param clazz 要被实例化的 Class
     * @param args 参数列表
     * @return 实例化后的对象
     */
    public static Object invokeConstructor(Class<?> clazz, Object... args) throws Exception {
        Class<?>[] parameterTypes = new Class<?>[args.length];
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }
        return Refs.invokeConstructorLimitArgTypes(clazz, args, parameterTypes);
    }
}
