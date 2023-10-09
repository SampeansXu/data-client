package com.hqwx.bigdata.client.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @Description: FieldUtil
 * @Author: XuShengBin
 * @Date: 2022-08-31
 * @Ver: v1.0 -create
 */
public class ReflectUtil {

    /**
     * 判断一个方法是一个常规的getter方法
     *
     * @param method method function
     * @return 是否是一个常规的getter方法
     */
    public static boolean isNotGeneralGetterMethod(Method method) {
        if (method == null) {
            return true;
        }
        return !Modifier.isPublic(method.getModifiers()) || method.getReturnType() == void.class;
    }

    /**
     * 判断一个方法是一个常规的setter方法
     *
     * @param method method function
     * @return 是否是一个常规的setter方法
     */
    public static boolean isNotGeneralSetterMethod(Method method) {
        if (method == null) {
            return true;
        }
        return !Modifier.isPublic(method.getModifiers()) || method.getReturnType() != void.class;
    }

    /**
     * 判断一个字段类型是不是一个常规属性，比如：static final 修饰的字段暂时不属于一个常规字段
     *
     * @param field 字段类型
     * @return 是否是一个常规属性
     */
    public static boolean isNotGeneralProperty(Field field) {
        if (field == null) {
            return true;
        }
        return Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
    }

    /**
     * 获取某一字段的get方法名称
     *
     * @param field 字段obj
     * @return getter function name
     */
    public static String getGetterName(Field field) {
        String propertyName = field.getName();
        StringBuilder sb = new StringBuilder();
        if (booleanType(field.getType()) && propertyName.startsWith("is")) {
            //如果字段是boolean类型的
            sb.append("is");
            sb.append(propertyName.substring(2).substring(0, 1).toUpperCase());
            sb.append(propertyName.substring(2).substring(1));

        } else {
            sb.append("get");
            sb.append(propertyName.substring(0, 1).toUpperCase());
            sb.append(propertyName.substring(1));
        }
        return sb.toString();
    }

    /**
     * 获取某一字段的set方法名称
     *
     * @param field 字段obj
     * @return setter function name
     */
    public static String getSetterName(Field field) {
        String propertyName = field.getName();
        StringBuilder sb = new StringBuilder();
        if (booleanType(field.getType()) && propertyName.startsWith("is")) {
            //如果字段是boolean类型的
            sb.append("set");
            sb.append(propertyName.substring(2).substring(0, 1).toUpperCase());
            sb.append(propertyName.substring(2).substring(1));

        } else {
            sb.append("set");
            sb.append(propertyName.substring(0, 1).toUpperCase());
            sb.append(propertyName.substring(1));
        }
        return sb.toString();
    }

    private static boolean booleanType(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }
}
