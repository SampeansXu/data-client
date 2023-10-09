package com.hqwx.bigdata.client.common.wrapper;

import com.hqwx.bigdata.client.phoenix.wrapper.PhoenixLambdaQueryWrapper;
import com.hqwx.bigdata.client.phoenix.wrapper.PhoenixQueryWrapper;

/**
 * @Description: BigData Wrappers
 * @Author: XuShengBin
 * @Date: 2022-09-01
 * @Ver: v1.0 -create
 */
public class BDWrappers {
    public interface Phoenix{
        static <T> PhoenixQueryWrapper<T> query(Class<T> entityClass) {
            return new PhoenixQueryWrapper(entityClass);
        }

        static <T> PhoenixLambdaQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
            return new PhoenixLambdaQueryWrapper(entityClass);
        }
    }
}
