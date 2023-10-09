package com.hqwx.bigdata.client.phoenix.utils;

import com.google.common.collect.Lists;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixOperationException;
import com.hqwx.bigdata.client.common.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @Description: PhoenixTemplateUtil
 * @Author: XuShengBin
 * @Date: 2022-09-01
 * @Ver: v1.0 -create
 */
public class PhoenixTemplateUtil {
    private static List<String> getRowColumnNames(ResultSet rowSet) throws SQLException {
        if(Objects.isNull(rowSet)){
            return null;
        }
        List<String> resultList = Lists.newArrayList();
        ResultSetMetaData metaData = rowSet.getMetaData();
        int count = metaData.getColumnCount();
        for(int i = 1; i<=count; i++) {
            resultList.add(metaData.getColumnName(i).toUpperCase());
        }
        return resultList;
    }

    private static Map<String, Object> rowToMap(ResultSet rowSet, ResultSetMetaData metaData) throws SQLException {
        if(Objects.isNull(rowSet) || Objects.isNull(metaData)){
            return null;
        }

        Map<String, Object> resultMap = new HashMap<>();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String key = metaData.getColumnLabel(i);
            Object value = rowSet.getObject(i);
            resultMap.put(key, value);
        }
        return resultMap;
    }
    /**
     * 把查询结果集row转换为Map类型的结构
     *
     * @param rowSet Result对象
     * @return Map结果的数据
     */
    public static List<Map<String, Object>> rowSetToMap(ResultSet rowSet) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if(Objects.isNull(rowSet)){
            return resultList;
        }

        while(rowSet.next()){
            ResultSetMetaData metaData = rowSet.getMetaData();
            Map<String, Object> resultMap = rowToMap(rowSet, metaData);
            if(Objects.nonNull(resultMap)){
                resultList.add(resultMap);
            }
        }

        return resultList;
    }

    /**
     * 查询结果集row到定义的JavaBean
     *
     * @param rowSet 数据集合
     * @param clazz  JavaBean Class
     * @param <T>    泛型类型
     * @return JavaBean之后的查询结果集
     * @throws Exception 异常抛出
     */
    private static <T> T rowToBean(ResultSet rowSet, Class<T> clazz) throws Exception {
        if(Objects.isNull(rowSet) || Objects.isNull(clazz)){
            return null;
        }

        T object = clazz.getDeclaredConstructor().newInstance();

        //获取结果的所有列名
        List<String> allColumnNames = getRowColumnNames(rowSet);

        //获取类方法
        Map<String, Method> allMethodMap = PhoenixReflectUtil.getAllMethodsMap(object.getClass());
        Method setMethod, getMethod;

        // 获取字段
        Map<String, Field> coln2FieldMap = new HashMap<>();
        Map<String, Field> annt2FieldMap = new HashMap<>();
        {
            final Field[] allFields = PhoenixReflectUtil.getAllFields(clazz);
            for (Field field : allFields) {
                coln2FieldMap.put(field.getName().toUpperCase(), field);
                annt2FieldMap.put(PhoenixReflectUtil.getColumnName(field).toUpperCase(), field);
            }
        }
        // 遍历字段
        for (String  columnName : allColumnNames) {
            Field field = annt2FieldMap.get(columnName);
            if(Objects.isNull(field)) {
                field = coln2FieldMap.get(columnName);
            }
            if (ReflectUtil.isNotGeneralProperty(field)) {
                continue;
            }
            setMethod = allMethodMap.getOrDefault(ReflectUtil.getSetterName(field), null);
            getMethod = allMethodMap.getOrDefault(ReflectUtil.getGetterName(field), null);
            if (ReflectUtil.isNotGeneralSetterMethod(setMethod)
                    || ReflectUtil.isNotGeneralGetterMethod(getMethod)) {
                throw new PhoenixOperationException("The setter and getter method is not definition for property: " + field.getName() + ".");
            }

            //根据字段赋值
            try {
                Object fieldVlaue = rowSet.getObject(columnName);
                setMethod.invoke(object, fieldVlaue);
            }catch(Exception e) {
                throw new PhoenixOperationException("ColumnName:" + columnName + " has exception: " + e.getMessage());
            }
        }

        return object;
    }
    public static <T> List<T> rowSetToBean(ResultSet rowSet, Class<T> clazz) throws Exception{
        List<T> resultList = new ArrayList<>();
        if(Objects.isNull(rowSet)){
            return resultList;
        }

        while (rowSet.next()){
            T obj = rowToBean(rowSet, clazz);
            if(Objects.nonNull(obj)){
                resultList.add(obj);
            }
        }

        return resultList;
    }
}
