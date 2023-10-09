package com.hqwx.bigdata.client.hbase.annotation;

import java.lang.annotation.*;

/**
 * @Description: HBase rowKey注解
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HBaseRowKey {
    boolean rowKey() default true;
}
