package com.hqwx.bigdata.client.common.sql.segments;

import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: OrderBySegmentList
 * @Author: XuShengBin
 * @Date: 2022-09-07
 * @Ver: v1.0 -create
 */
public class OrderBySegmentList extends AbstractISegmentList {
    public OrderBySegmentList() {
    }

    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        list.remove(0);
        List<ISqlSegment> sqlSegmentList = new ArrayList(list);
        list.clear();
        list.add(() -> {
            return (String)sqlSegmentList.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(" "));
        });
        return true;
    }

    protected String childrenSqlSegment() {
        return this.isEmpty() ? "" : (String)this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(",", " " + BaseSQLKeyword.ORDER_BY.getSqlSegment() + " ", ""));
    }
}
