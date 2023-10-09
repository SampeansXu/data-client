package com.hqwx.bigdata.client.hbase.annotation;

import java.lang.annotation.*;

/**
 * @Description: HBase表注解
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HBaseTable {
    /**
     * 表所属命名空间, 默认是default
     *
     * @return 表所属命名空间
     */
    String schema() default "default";

    /**
     * HBase表名, 默认是""
     *
     * @return 表名
     */
    String name() default "";

    /**
     * HBase表只有一个列族, 则设定uniqueFamily值; 否则不设定此值
     *
     * @return 表单列族名
     */
    String uniqueFamily() default "";
}
