package com.hqwx.bigdata.client.common.sql.segments;

import com.hqwx.bigdata.client.common.sql.constant.SqlConstant;
import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixOperationException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: MergeSegments
 * @Author: XuShengBin
 * @Date: 2022-09-06
 * @Ver: v1.0 -create
 */
public class MergeSegments {
    private final NormalSegmentList normal = new NormalSegmentList();
    private final GroupBySegmentList groupBy = new GroupBySegmentList();
    private final HavingSegmentList having = new HavingSegmentList();
    private final OrderBySegmentList orderBy = new OrderBySegmentList();
    private String sqlSegment = "";
    private boolean cacheSqlSegment = true;

    private Integer from = null;
    private Integer rows = null;

    public MergeSegments() {
    }

    public void add(ISqlSegment... iSqlSegments) {
        List<ISqlSegment> list = Arrays.asList(iSqlSegments);
        ISqlSegment firstSqlSegment = (ISqlSegment) list.get(0);
        if (MatchSegment.ORDER_BY.match(firstSqlSegment)) {
            this.orderBy.addAll(list);
        } else if (MatchSegment.GROUP_BY.match(firstSqlSegment)) {
            this.groupBy.addAll(list);
        } else if (MatchSegment.HAVING.match(firstSqlSegment)) {
            this.having.addAll(list);
        } else {
            this.normal.addAll(list);
        }

        this.cacheSqlSegment = false;
    }

    public void setLimit(Integer from, int rows) {
        if (rows < 0) {
            throw new PhoenixOperationException("rows is less than zero");
        }
        this.from = from;
        this.rows = rows;

        this.cacheSqlSegment = false;
    }

    public String getSqlSegment() {
        if (this.cacheSqlSegment) {
            return this.sqlSegment;
        } else {
            this.cacheSqlSegment = true;

            this.sqlSegment = this.normal.getSqlSegment()
                    + this.groupBy.getSqlSegment() + this.having.getSqlSegment() + this.orderBy.getSqlSegment()
                    + SqlConstant.Separator_Space + this.getLimit();

            return this.sqlSegment;
        }
    }

    private String getLimit() {
        String strLimit = "";
        if (Objects.nonNull(this.rows) && this.rows>=0) {
            strLimit += BaseSQLKeyword.LIMIT + SqlConstant.Separator_Space;
            if(Objects.nonNull(this.from) && this.from>=0) {
                strLimit += this.from + SqlConstant.Separator_Comma + this.rows;
            }else {
                strLimit += this.rows;
            }
        }

        return strLimit;
    }

    public void clear() {
        this.sqlSegment = "";
        this.cacheSqlSegment = true;
        this.from = null;
        this.rows = null;
        this.normal.clear();
        this.groupBy.clear();
        this.having.clear();
        this.orderBy.clear();
    }

    public NormalSegmentList getNormal() {
        return this.normal;
    }

    public GroupBySegmentList getGroupBy() {
        return this.groupBy;
    }

    public HavingSegmentList getHaving() {
        return this.having;
    }

    public OrderBySegmentList getOrderBy() {
        return this.orderBy;
    }
}
