package com.hqwx.bigdata.client.phoenix.annotation;

import java.lang.annotation.*;

/**
 * @Description: Phoenix 字段注解
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PhoenixColumn {
    /**
     * 字段名, 建议用"_"分隔字段名
     * 例如: BASE_INFO
     *
     * @return 字段名
     */
    String name() default "";

    /**
     * 对应HBase的列簇名, 忽略通用列簇名，为该字段特指一个列簇名
     *
     * @return 列簇名
     */
    String family() default "";

    /**
     * 是否将字段名全部转为大写, 默认false
     *
     * @return 是否为字段名转大写，true-转为大写 false-不转换
     */
    boolean toUpperCase() default false;
}
