package com.hqwx.bigdata.client.phoenix.annotation;

import com.hqwx.bigdata.client.phoenix.constant.PhoenixConstant;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Description: PhoenixMapper注解
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface PhoenixMapper {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";

    /**
     * DataSource名称
     * @return
     */
    String dataSourceName() default PhoenixConstant.DataSource_Name_Default;
}
