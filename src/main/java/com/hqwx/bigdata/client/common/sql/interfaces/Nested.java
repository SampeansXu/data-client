package com.hqwx.bigdata.client.common.sql.interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @Description: Nested
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public interface Nested<Param, Children> extends Serializable {
    default Children and(Consumer<Param> consumer) {
        return this.and(true, consumer);
    }

    Children and(boolean condition, Consumer<Param> consumer);

    default Children or(Consumer<Param> consumer) {
        return this.or(true, consumer);
    }

    Children or(boolean condition, Consumer<Param> consumer);

    default Children nested(Consumer<Param> consumer) {
        return this.nested(true, consumer);
    }

    Children nested(boolean condition, Consumer<Param> consumer);

    default Children not(Consumer<Param> consumer) {
        return this.not(true, consumer);
    }

    Children not(boolean condition, Consumer<Param> consumer);
}
