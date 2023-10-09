package com.hqwx.bigdata.client.common.sql.utils;

import com.hqwx.bigdata.client.common.sql.enums.SqlLike;

/**
 * @Description: SqlUtil
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public class SqlUtil {
    public static String concatLike(Object str, SqlLike type) {
        String strLike = "";
        switch (type) {
            case LEFT:
                strLike = "%" + str;
                break;
            case RIGHT:
                strLike = str + "%";
                break;
            default:
                strLike = "%" + str + "%";
                break;
        }

        return strLike;
    }
}
