package com.hqwx.bigdata.client.common.lambda;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface BDSFunction<T,R> extends Function<T,R>, Serializable {
}
