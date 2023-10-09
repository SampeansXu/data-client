package com.hqwx.bigdata.client.common.sql.segments;

import java.io.Serializable;

/**
 * @Description: ISqlSegment
 * @Author: XuShengBin
 * @Date: 2022-09-07
 * @Ver: v1.0 -create
 */
@FunctionalInterface
public interface ISqlSegment extends Serializable {
    String getSqlSegment();
}
