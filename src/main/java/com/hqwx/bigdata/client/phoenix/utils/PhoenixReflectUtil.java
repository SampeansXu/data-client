package com.hqwx.bigdata.client.phoenix.utils;


import com.hqwx.bigdata.client.common.sql.constant.SqlConstant;
import com.hqwx.bigdata.client.common.utils.FieldOrTableNameUtil;
import com.hqwx.bigdata.client.common.utils.ReflectUtil;
import com.hqwx.bigdata.client.phoenix.annotation.PhoenixColumn;
import com.hqwx.bigdata.client.phoenix.annotation.PhoenixMapper;
import com.hqwx.bigdata.client.phoenix.annotation.PhoenixTable;
import com.hqwx.bigdata.client.phoenix.constant.PhoenixConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description: PhoenixReflectUtil
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
public class PhoenixReflectUtil {
    private static Map<Class, WeakReference<Method[]>> Cache_AllMethods = new ConcurrentHashMap<>();
    private static Map<Class, WeakReference<Method[]>> Cache_SuperAllMethods = new ConcurrentHashMap<>();
    private static Map<Class, WeakReference<Field[]>> Cache_AllFields = new ConcurrentHashMap<>();
    private static Map<Class, WeakReference<Field[]>> Cache_SuperAllFields = new ConcurrentHashMap<>();

    /**
     * 获取查询字段
     *
     * @param fields 指定字段名列表, 为空或null则获取全部
     * @param clazz  数据实体类的类型
     * @return 查询的完整字段, 如: RowKey,BASE_INFO.ID,...,OTHER_INFO.MEMO
     */
    public static String getSelectColumnsByFields(List<String> fields, Class clazz) {
        String resultSql = "";
        final Field[] allFields = getAllFields(clazz);
        for (Field field : allFields) {
            if (ReflectUtil.isNotGeneralProperty(field)) {
                continue;
            }
            if (Objects.nonNull(fields) && fields.size() > 0) {
                if (!fields.contains(field.getName())) {
                    continue;
                }
            }

            String columnName = getFullColumnName(field);
            if (resultSql.length() > 0) {
                resultSql += SqlConstant.Separator_Comma + " ";
            }
            resultSql += columnName;
        }

        if(StringUtils.isEmpty(resultSql)) {
            resultSql = SqlConstant.Separator_Asterisk;
        }

        return resultSql;
    }
    public static String getSelectByColumns(List<String> columns, Class clazz) {
        String resultSql = "";
        if (CollectionUtils.isEmpty(columns)) {
            return resultSql = getSelectColumnsByFields(null, clazz);
        }
        for (String column : columns) {
            String columnName = getFullColumnName(column, clazz);
            if(StringUtils.isBlank(columnName)) {
                columnName = column;
            }

            if (resultSql.length() > 0) {
                resultSql += SqlConstant.Separator_Comma + " ";
            }
            resultSql += columnName;
        }

        if(StringUtils.isEmpty(resultSql)) {
            resultSql = SqlConstant.Separator_Asterisk;
        }

        return resultSql;
    }

    /**
     * 获取类的所有方法
     *
     * @param clazz 数据实体类的类型
     * @return 所有方法的 map <方法名,方法>
     */
    public static Map<String, Method> getAllMethodsMap(Class<?> clazz) {
        Method[] methods = getAllMethods(clazz);
        return Arrays.stream(methods).collect(Collectors.toMap(Method::getName, field -> field));
    }

    /**
     * 获取类的所有方法，包括其所有父类的方法
     *
     * @param clazz 数据实体类的类型
     * @return 方法数组
     */
    public static Method[] getAllMethods(Class<?> clazz) {
        WeakReference<Method[]> methodCache = Cache_AllMethods.get(clazz);
        if(Objects.isNull(methodCache) || Objects.isNull(methodCache.get())) {
            methodCache = new WeakReference(clazz.getDeclaredMethods());
        }
        Method[] methods = methodCache.get();
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz.equals(Object.class)) {
            return methods;
        }

        WeakReference<Method[]> superMethodCache = Cache_SuperAllMethods.get(superClazz);
        if(Objects.isNull(superMethodCache) || Objects.isNull(superMethodCache.get())) {
            superMethodCache = new WeakReference(superClazz.getDeclaredMethods());
        }
        Method[] tableSuperMethods = superMethodCache.get();
        Method[] superMethods = new Method[methods.length + tableSuperMethods.length];
        System.arraycopy(methods, 0, superMethods, 0, methods.length);
        System.arraycopy(tableSuperMethods, 0, superMethods, methods.length, tableSuperMethods.length);
        return getSuperClassMethods(superMethods, superClazz);
    }

    /**
     * 递归获取所有父类的方法
     *
     * @param methods 方法列表
     * @param clazz   数据实体类的类型
     * @return 方法数组
     */
    public static Method[] getSuperClassMethods(Method[] methods, Class<?> clazz) {
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz.equals(Object.class)) {
            return methods;
        }

        WeakReference<Method[]> superMethodCache = Cache_SuperAllMethods.get(superClazz);
        if(Objects.isNull(superMethodCache) || Objects.isNull(superMethodCache.get())) {
            superMethodCache = new WeakReference(superClazz.getDeclaredMethods());
        }
        Method[] superMethods = superMethodCache.get();
        Method[] c = new Method[methods.length + superMethods.length];
        System.arraycopy(methods, 0, c, 0, methods.length);
        System.arraycopy(superMethods, 0, c, methods.length, superMethods.length);
        getSuperClassMethods(c, superClazz);
        return c;
    }

    /**
     * 获取类的所有字段
     *
     * @param clazz 数据实体类的类型
     * @return 所有字段的 map <字段名,字段>
     */
    public static Map<String, Field> getAllFieldsMap(Class<?> clazz) {
        Field[] fields = getAllFields(clazz);
        return Arrays.stream(fields).collect(Collectors.toMap(Field::getName, field -> field));
    }

    /**
     * 获取类所有的字段，包括其所有父类的字段
     *
     * @param clazz 数据实体类的类型
     * @return 字段数组
     */
    public static Field[] getAllFields(Class<?> clazz) {
        WeakReference<Field[]> fieldCache = Cache_AllFields.get(clazz);
        if(Objects.isNull(fieldCache) || Objects.isNull(fieldCache.get())) {
            fieldCache = new WeakReference<>(clazz.getDeclaredFields());
        }
        Field[] tableFields = fieldCache.get();
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz.equals(Object.class)) {
            return tableFields;
        }

        WeakReference<Field[]> superFieldCache = Cache_SuperAllFields.get(superClazz);
        if(Objects.isNull(superFieldCache) || Objects.isNull(superFieldCache.get())) {
            superFieldCache = new WeakReference<>(superClazz.getDeclaredFields());
        }
        Field[] tableSuperFields = superFieldCache.get();
        Field[] superFields = new Field[tableFields.length + tableSuperFields.length];
        System.arraycopy(tableFields, 0, superFields, 0, tableFields.length);
        System.arraycopy(tableSuperFields, 0, superFields, tableFields.length, tableSuperFields.length);
        return getSuperClassFields(superFields, superClazz);
    }

    /**
     * 递归获取所有的父类字段
     *
     * @param fields 父类字段
     * @param clazz  数据实体类的类型
     * @return 字段数组
     */
    public static Field[] getSuperClassFields(Field[] fields, Class<?> clazz) {
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz.equals(Object.class)) {
            return fields;
        }

        WeakReference<Field[]> superFieldCache = Cache_SuperAllFields.get(superClazz);
        if(Objects.isNull(superFieldCache) || Objects.isNull(superFieldCache.get())) {
            superFieldCache = new WeakReference<>(superClazz.getDeclaredFields());
        }
        Field[] superFields = superFieldCache.get();
        Field[] c = new Field[fields.length + superFields.length];
        System.arraycopy(fields, 0, c, 0, fields.length);
        System.arraycopy(superFields, 0, c, fields.length, superFields.length);
        getSuperClassFields(c, superClazz);
        return c;
    }

    /**
     * 获取Phoenix表或视图名，如果没有{@link PhoenixTable}注解，则取数据实体下划线格式的类名作为表名，
     *
     * @param clazz 数据实体类的类型
     * @return Phoenix的table或view名
     */
    public static String getTableName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        String tableName = FieldOrTableNameUtil.underscoreName(className);
        if (clazz.isAnnotationPresent(PhoenixTable.class)) {
            PhoenixTable table = clazz.getAnnotation(PhoenixTable.class);
            if (StringUtils.isNotBlank(table.name())) {
                tableName = table.name();
            }
        }

        return tableName;
    }

    /**
     * 获取DataSource名，{@link PhoenixMapper}注解:dataSourceName
     *
     * @param clazz 数据实体类的类型
     * @return Phoenix的DataSource名
     */
    public static String getDataSourceName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        String dataSourceName = FieldOrTableNameUtil.underscoreName(className);
        if (clazz.isAnnotationPresent(PhoenixMapper.class)) {
            PhoenixMapper mapper = clazz.getAnnotation(PhoenixMapper.class);
            if (StringUtils.isNotBlank(mapper.dataSourceName())) {
                dataSourceName = mapper.dataSourceName();
            }
            return dataSourceName;
        } else {
            return PhoenixConstant.DataSource_Name_Default;
        }
    }

    /**
     * 获取完整字段名, {@link PhoenixColumn}注解:family.name 如: BASE_INFO.NAME
     * 如果没有指定全局列簇名，则必须为每一个字段指定列簇名，每一个字段注解中指定的列簇名的优先级最高。
     * 通过注解toUpperCase，可以返回的字段名大写
     *
     * @param field 字段
     * @return 完整字段名，例如：BASE_INFO.NAME
     */
    public static String getFullColumnName(Field field) {
        String fieldName = FieldOrTableNameUtil.underscoreName(field.getName());
        if (field.isAnnotationPresent(PhoenixColumn.class)) {
            PhoenixColumn column = field.getAnnotation(PhoenixColumn.class);
            if (StringUtils.isNotBlank(column.name())) {
                fieldName = column.name();
            }
            if (StringUtils.isNotBlank(column.family())) {
                String columnFamily = column.family();
                fieldName = columnFamily + PhoenixConstant.Separator_Property + fieldName;
            }

            if (column.toUpperCase()) {
                fieldName = fieldName.toUpperCase();
            }
        }

        return fieldName;
    }
    public static String getFullColumnName(String fieldName, Class clazz) {
        String result = "";
        if(StringUtils.isBlank(fieldName)) {
            return result;
        }
        final Field[] allFields = getAllFields(clazz);
        for (Field field : allFields) {
            if (ReflectUtil.isNotGeneralProperty(field)) {
                continue;
            }
            if(!fieldName.equals(field.getName())) {
                continue;
            }

            String columnName = getFullColumnName(field);
            result = columnName;
            break;
        }

        return result;
    }

    /**
     * 获取字段名, {@link PhoenixColumn}注解:name 如: NAME
     * 通过上述注解toUpperCase，可以返回的字段名大写
     *
     * @param field 字段
     * @return 字段名，例如：NAME
     */
    public static String getColumnName(Field field) {
        String fieldName = FieldOrTableNameUtil.underscoreName(field.getName());
        if (field.isAnnotationPresent(PhoenixColumn.class)) {
            PhoenixColumn column = field.getAnnotation(PhoenixColumn.class);
            if (StringUtils.isNotBlank(column.name())) {
                fieldName = column.name();
            }

            if (column.toUpperCase()) {
                fieldName = fieldName.toUpperCase();
            }
        }

        return fieldName;
    }
    public static String getColumnName(String fieldName, Class clazz) {
        String result = "";
        if(StringUtils.isBlank(fieldName)) {
            return result;
        }
        final Field[] allFields = getAllFields(clazz);
        for (Field field : allFields) {
            if (ReflectUtil.isNotGeneralProperty(field)) {
                continue;
            }
            if(!fieldName.equals(field.getName())) {
                continue;
            }

            String columnName = getColumnName(field);
            result = columnName;
            break;
        }

        return result;
    }
}
