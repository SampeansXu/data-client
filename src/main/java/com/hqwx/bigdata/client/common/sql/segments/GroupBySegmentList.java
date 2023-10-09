package com.hqwx.bigdata.client.common.sql.segments;

import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: GroupBySegmentList
 * @Author: XuShengBin
 * @Date: 2022-09-07
 * @Ver: v1.0 -create
 */
public class GroupBySegmentList extends AbstractISegmentList {
    public GroupBySegmentList() {
    }

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        list.remove(0);
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        return this.isEmpty() ? "" : (String)this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(",", " " + BaseSQLKeyword.GROUP_BY + " ", ""));
    }
}
