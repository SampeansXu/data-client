package com.hqwx.bigdata.client.common.sql.interfaces;

import java.io.Serializable;

/**
 * @Description: Transfer
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public interface Transfer<Children, R> extends Serializable {
    default Children eq(R column, Object val) {
        return this.eq(true, column, val);
    }

    Children eq(boolean condition, R column, Object val);

    default Children ne(R column, Object val) {
        return this.ne(true, column, val);
    }

    Children ne(boolean condition, R column, Object val);

    default Children gt(R column, Object val) {
        return this.gt(true, column, val);
    }

    Children gt(boolean condition, R column, Object val);

    default Children ge(R column, Object val) {
        return this.ge(true, column, val);
    }

    Children ge(boolean condition, R column, Object val);

    default Children lt(R column, Object val) {
        return this.lt(true, column, val);
    }

    Children lt(boolean condition, R column, Object val);

    default Children le(R column, Object val) {
        return this.le(true, column, val);
    }

    Children le(boolean condition, R column, Object val);

    default Children between(R column, Object val1, Object val2) {
        return this.between(true, column, val1, val2);
    }

    Children between(boolean condition, R column, Object val1, Object val2);

    default Children like(R column, Object val) {
        return this.like(true, column, val);
    }

    Children like(boolean condition, R column, Object val);

    default Children likeLeft(R column, Object val) {
        return this.likeLeft(true, column, val);
    }

    Children likeLeft(boolean condition, R column, Object val);

    default Children likeRight(R column, Object val) {
        return this.likeRight(true, column, val);
    }

    Children likeRight(boolean condition, R column, Object val);
}
