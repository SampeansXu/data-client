package com.hqwx.bigdata.client.common.sql.segments;

import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: NormalSegmentList
 * @Author: XuShengBin
 * @Date: 2022-09-07
 * @Ver: v1.0 -create
 */
public class NormalSegmentList extends AbstractISegmentList{
    private boolean executeNot = true;

    NormalSegmentList() {
        this.flushLastValue = true;
    }

    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (list.size() == 1) {
            if (MatchSegment.NOT.match(firstSegment)) {
                this.executeNot = false;
                return false;
            }

            if (this.isEmpty()) {
                return false;
            }

            boolean matchLastAnd = MatchSegment.AND.match(this.lastValue);
            boolean matchLastOr = MatchSegment.OR.match(this.lastValue);
            if (matchLastAnd || matchLastOr) {
                if (matchLastAnd && MatchSegment.AND.match(firstSegment)) {
                    return false;
                }

                if (matchLastOr && MatchSegment.OR.match(firstSegment)) {
                    return false;
                }

                this.removeAndFlushLast();
            }
        } else {
            if (MatchSegment.APPLY.match(firstSegment)) {
                list.remove(0);
            }

            if (!MatchSegment.AND_OR.match(this.lastValue) && !this.isEmpty()) {
                this.add(BaseSQLKeyword.AND);
            }

            if (!this.executeNot) {
                list.add(0, BaseSQLKeyword.NOT);
                this.executeNot = true;
            }
        }

        return true;
    }

    protected String childrenSqlSegment() {
        if (MatchSegment.AND_OR.match(this.lastValue)) {
            this.removeAndFlushLast();
        }

        String str = (String)this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(" "));
        return "(" + str + ")";
    }

    public void clear() {
        super.clear();
        this.flushLastValue = true;
        this.executeNot = true;
    }
}
