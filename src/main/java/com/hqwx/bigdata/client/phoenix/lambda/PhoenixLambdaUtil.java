package com.hqwx.bigdata.client.phoenix.lambda;

import com.hqwx.bigdata.client.common.lambda.BDSFunction;
import com.hqwx.bigdata.client.common.lambda.SerializedLambda;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: PhoenixLambdaUtil
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public class PhoenixLambdaUtil {
    private static Map<Class, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();
    public static String getColumnName(Object func){
        if(func instanceof String){
            return (String) func;
        }else if (func instanceof BDSFunction){
            String methodName = PhoenixLambdaUtil.extract((BDSFunction)func).getImplMethodName();
            String name = resolveFieldName(methodName);
            return name;
        }
        return null;
    }
    public static String firstToLowerCase(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }

    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);

        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);

        }

        return firstToLowerCase(getMethodName);

    }

    private static SerializedLambda getLambda(BDSFunction func){
        SerializedLambda lambda = null;
        try {
            lambda = SerializedLambda.extract(func);
        }catch(Exception e){
            lambda = null;
        }

        if(Objects.isNull(lambda)){
            try {
                Method method = func.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda)method.invoke(func);
            }catch(Exception e){
                lambda = null;
            }
        }

        return lambda;
    }
    public static SerializedLambda extract(BDSFunction func) {
        Class clazz = func.getClass();
        WeakReference<SerializedLambda> lambda = FUNC_CACHE.get(clazz);
        if(Objects.isNull(lambda)
                || Objects.isNull(lambda.get())) {
            lambda = new WeakReference<>(getLambda(func));
            FUNC_CACHE.put(clazz, lambda);
        }

        return lambda.get();
    }
}
