package com.hqwx.bigdata.client.phoenix.enums;

import com.hqwx.bigdata.client.common.sql.segments.ISqlSegment;

/**
 * @Description: Phoenix SQL语法关键字
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public enum PhoenixSQLKeyword implements ISqlSegment {
    // Phoenix Sql语句语法
    UPSERT_INTO("UPSERT INTO"),
    ;

    private final String keyword;

    private PhoenixSQLKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getSqlSegment() {
        return keyword;
    }

    @Override
    public String toString() {
        return getSqlSegment();
    }
}
