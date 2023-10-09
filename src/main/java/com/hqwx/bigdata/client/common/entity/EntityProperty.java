package com.hqwx.bigdata.client.common.entity;

import java.lang.reflect.Type;

/**
 * @Description: 实体字段属性
 * @Author: XuShengBin
 * @Date: 2022-09-02
 * @Ver: v1.0 -create
 */
public class EntityProperty {
    private String fieldName;
    private String columnName;
    private String fullColumnName;
    private Type type;
    private Object value;

    public EntityProperty() {
    }

    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFullColumnName() {
        return fullColumnName;
    }
    public void setFullColumnName(String fullColumnName) {
        this.fullColumnName = fullColumnName;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
}
