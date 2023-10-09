package com.hqwx.bigdata.client.common.sql.segments;

import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;
import com.hqwx.bigdata.client.phoenix.enums.PhoenixWrapperKeyword;

import java.util.function.Predicate;

/**
 * @Description: MatchSegment
 * @Author: XuShengBin
 * @Date: 2022-09-07
 * @Ver: v1.0 -create
 */
public enum MatchSegment {
    //语句段
    GROUP_BY((i) -> {
        return i == BaseSQLKeyword.GROUP_BY;
    }),
    ORDER_BY((i) -> {
        return i == BaseSQLKeyword.ORDER_BY;
    }),
    NOT((i) -> {
        return i == BaseSQLKeyword.NOT;
    }),
    AND((i) -> {
        return i == BaseSQLKeyword.AND;
    }),
    OR((i) -> {
        return i == BaseSQLKeyword.OR;
    }),
    AND_OR((i) -> {
        return i == BaseSQLKeyword.AND || i == BaseSQLKeyword.OR;
    }),
//    EXISTS((i) -> {
//        return i == PhoenixSQLKeyword.EXISTS;
//    }),
//    LIMIT((i) -> {
//        return i == BaseSQLKeyword.LIMIT;
//    }),
    HAVING((i) -> {
        return i == BaseSQLKeyword.HAVING;
    }),
    APPLY((i) -> {
        return i == PhoenixWrapperKeyword.APPLY;
    });

    private final Predicate<ISqlSegment> predicate;

    public boolean match(ISqlSegment segment) {
        return this.getPredicate().test(segment);
    }

    public Predicate<ISqlSegment> getPredicate() {
        return this.predicate;
    }

    private MatchSegment(final Predicate<ISqlSegment> predicate) {
        this.predicate = predicate;
    }
}
