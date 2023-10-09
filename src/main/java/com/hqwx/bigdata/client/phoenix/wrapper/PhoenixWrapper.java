package com.hqwx.bigdata.client.phoenix.wrapper;

import com.hqwx.bigdata.client.common.sql.constant.SqlConstant;
import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;
import com.hqwx.bigdata.client.common.sql.segments.ISqlSegment;
import com.hqwx.bigdata.client.common.sql.segments.MergeSegments;
import com.hqwx.bigdata.client.common.sql.segments.NormalSegmentList;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Description: PhoenixWrapper
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public abstract class PhoenixWrapper<T> implements ISqlSegment {
    public PhoenixWrapper() {
    }

    public abstract T getEntity();

    public List<String> getSqlSelect() {
        return null;
    }
    public String getLast() {
        return null;
    }
    public String getFirst() {
        return null;
    }

//    public String getSqlComment() {
//        return null;
//    }

    public abstract MergeSegments getExpression();

    public String getCustomSqlSegment() {
        MergeSegments expression = this.getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = this.getSqlSegment();
            if (StringUtils.isNotBlank(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                }

                return BaseSQLKeyword.WHERE + SqlConstant.Separator_Space + sqlSegment;
            }
        }

        return "";
    }

    public abstract void clear();
}
