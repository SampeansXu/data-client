package com.hqwx.bigdata.client.phoenix.annotation;

import com.hqwx.bigdata.client.phoenix.constant.PhoenixConstant;

import java.lang.annotation.*;

/**
 * @Description: Phoenix 表或视图注解
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PhoenixTable {
    interface Type {
        String Table = PhoenixConstant.PhoenixTable_Type.Table;
        String View = PhoenixConstant.PhoenixTable_Type.View;
    }

    /**
     * phoenix DB类型(table 或 view). 默认是"table".
     *
     * @return phoenix DB类型
     */
    String type() default Type.Table;

    /**
     * phoenix 表或视图名
     *
     * @return phoenix表或视图名
     */
    String name() default "";
}
