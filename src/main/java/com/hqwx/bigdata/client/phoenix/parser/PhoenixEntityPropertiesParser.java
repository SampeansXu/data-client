package com.hqwx.bigdata.client.phoenix.parser;

import com.hqwx.bigdata.client.common.entity.EntityProperty;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixOperationException;
import com.hqwx.bigdata.client.phoenix.utils.PhoenixReflectUtil;
import com.hqwx.bigdata.client.common.utils.ReflectUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: PhoenixEntityPropertiesContext
 * @Author: XuShengBin
 * @Date: 2022-09-02
 * @Ver: v1.0 -create
 */
public class PhoenixEntityPropertiesParser<T> {

    private Map<String, EntityProperty> field2PropertyMap = new LinkedHashMap<String, EntityProperty>();

    public PhoenixEntityPropertiesParser(T entity) throws PhoenixOperationException {
        if(Objects.isNull(entity)){
            throw new PhoenixOperationException(String.format("entity is null"));
        }

        initialize(entity);
    }

    public Map<String, EntityProperty> getField2PropertyMap() {
        return this.field2PropertyMap;
    }

    /**
     * 获取Bean的字段对应的值
     * @param entity
     * @param fieldName
     * @param defaultValue
     * @return
     * @throws Exception
     */
    private Object getProperty(T entity, String fieldName, Object defaultValue) {
        try {
            Class<?> clazz = entity.getClass();
            // 获取Bean的某个属性的描述符
            PropertyDescriptor proDescriptor = new PropertyDescriptor(fieldName, clazz);
            Method getMethod = proDescriptor.getReadMethod();
            // 读取属性值
            Object objValue = getMethod.invoke(entity);
            if (Objects.isNull(objValue) || "null".equals(objValue)){
                return defaultValue;
            }

            return objValue;
        }catch (Exception e) {
            throw new PhoenixOperationException(String.format("entity get value of filed:%s failed. Exception:%s",
                    fieldName, e.getMessage()));
        }
    }
    private void initialize(T entity) throws PhoenixOperationException{
        Class<?> clazz = entity.getClass();
        Field[] allFields = PhoenixReflectUtil.getAllFields(clazz);
        if(Objects.isNull(allFields) || allFields.length <= 0){
            throw new PhoenixOperationException(String.format("entity Class Field is null"));
        }

        for (Field field : allFields) {
            if (ReflectUtil.isNotGeneralProperty(field)) {
                continue;
            }

            Object value = this.getProperty(entity, field.getName(), null);
            EntityProperty entityProperty = new EntityProperty();
            entityProperty.setFieldName(field.getName());
            entityProperty.setColumnName(PhoenixReflectUtil.getColumnName(field));
            entityProperty.setFullColumnName(PhoenixReflectUtil.getFullColumnName(field));
            entityProperty.setType(field.getType());
            entityProperty.setValue(value);
            this.field2PropertyMap.put(field.getName(), entityProperty);
        }
    }
}
