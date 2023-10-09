package com.hqwx.bigdata.client.common.sql.enums;

import com.hqwx.bigdata.client.common.sql.segments.ISqlSegment;

/**
 * @Description: SQL基础语法关键字
 * @Author: XuShengBin
 * @Date: 2022-08-30
 * @Ver: v1.0 -create
 */
public enum BaseSQLKeyword implements ISqlSegment {
    // Sql语句语法
    SELECT("SELECT"),
    DELETE("DELETE"),
    FROM("FROM"),
    WHERE("WHERE"),
    HAVING("HAVING"),
    BETWEEN("BETWEEN"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    ORDER_BY("ORDER BY"),
    ASC("ASC"),
    DESC("DESC"),
    LIMIT("LIMIT"),

    //集合
    GROUP_BY("GROUP BY"),
    COUNT("COUNT"),
    AVG("AVG"),
    SUM("SUM"),
    MAX("MAX"),
    MIN("MIN"),

    //条件运算
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    //LIKE_CASE_INSENSITIVE("ILIKE"),
    EQ("="),
    NE("!="),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    //CONTAINS("CONTAINS"),
    //WITHIN("WITHIN"),
    ;

    private final String keyword;

    private BaseSQLKeyword(String keyword) {
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
