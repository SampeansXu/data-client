package com.hqwx.bigdata.client.phoenix.enums;

import com.hqwx.bigdata.client.common.sql.segments.ISqlSegment;

/**
 * @Description: PhoenixWrapperKeyword
 * @Author: XuShengBin
 * @Date: 2022-09-07
 * @Ver: v1.0 -create
 */
public enum PhoenixWrapperKeyword implements ISqlSegment {
    //apply
    APPLY((String)null);

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }

    private PhoenixWrapperKeyword(final String keyword) {
        this.keyword = keyword;
    }
}
