package com.hqwx.bigdata.client.phoenix.parser;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.hqwx.bigdata.client.common.sql.constant.SqlConstant;
import com.hqwx.bigdata.client.phoenix.utils.PhoenixDateFormatUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @Description: PhoenixEntityPropertySQLUtil
 * @Author: XuShengBin
 * @Date: 2022-09-05
 * @Ver: v1.0 -create
 */
public class PhoenixSQLUtil {

    private static final Function<Object, String> toSQLStr = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return property2SQLStr(o);
        }
    };
    private static String sanitizeString(String stringValue) {
        if (stringValue.contains(SqlConstant.Separator_Apostrophe)) {
            stringValue = stringValue.replace("'", "''");
        }
        if (stringValue.contains("\\")) {
            stringValue = stringValue.replace("\\", "\\\\");
        }
        return stringValue;
    }
    /**
     * 字段或属性值,转换成SQL字段对应的值
     *
     * @param obj
     * @return
     */
    public static String property2SQLStr(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        if (obj instanceof Object[]) {
            return "[" + Joiner.on(SqlConstant.Separator_Comma).join(Lists.transform(Arrays.asList((Object[]) obj), toSQLStr)) + "]";
        } else if (obj instanceof String) {
            return "'" + sanitizeString((String) obj) + "'";
        } else if (obj instanceof Character) {
            return "'" + sanitizeString(obj.toString()) + "'";
        } else if (obj instanceof Date) {
            return PhoenixDateFormatUtil.formatDate((Date) obj);
        } else if (obj != null) {
            return obj.toString();
        }
        return null;
    }
}
