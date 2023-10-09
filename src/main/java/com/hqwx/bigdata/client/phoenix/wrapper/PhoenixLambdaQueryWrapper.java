package com.hqwx.bigdata.client.phoenix.wrapper;

import com.hqwx.bigdata.client.common.lambda.BDSFunction;
import com.hqwx.bigdata.client.common.sql.segments.MergeSegments;
import com.hqwx.bigdata.client.phoenix.lambda.PhoenixLambdaUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: PhoenixLamdaQueryWrapper
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public class PhoenixLambdaQueryWrapper<T> extends PhoenixBaseWrapper<T, BDSFunction<T,?>, PhoenixLambdaQueryWrapper<T>> {
    private List<String> sqlSelect;

//    public PhoenixLambdaQueryWrapper() {
//        this(null);
//    }

    public PhoenixLambdaQueryWrapper(Class<T> entityClass) {
        this.sqlSelect = new ArrayList<>();
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    private PhoenixLambdaQueryWrapper(T entity, Class<T> entityClass, List<String> sqlSelect, MergeSegments mergeSegments, String lastSql, String sqlComment, String sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.sqlSelect = sqlSelect;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    public PhoenixLambdaQueryWrapper<T> addSqlSelectByName(String...columns){
        List<String> list = Arrays.asList(columns);
        return this.addSqlSelectByName(list);
    }
    public PhoenixLambdaQueryWrapper<T> addSqlSelectByName(boolean condition, String...columns){
        List<String> list = Arrays.asList(columns);
        return this.addSqlSelectByName(condition, list);
    }

    public PhoenixLambdaQueryWrapper<T> addSqlSelectByName(List<String> columns){
        return this.addSqlSelectByName(true, columns);
    }
    public PhoenixLambdaQueryWrapper<T> addSqlSelectByName(boolean condition, List<String> columns){
        if(condition && !CollectionUtils.isEmpty(columns)) {
            if (Objects.isNull(this.sqlSelect)) {
                this.sqlSelect = new ArrayList<>();
            }
            this.sqlSelect.addAll(columns);
        }

        return (PhoenixLambdaQueryWrapper)this.typedThis;
    }

    public PhoenixLambdaQueryWrapper<T> addSqlSelect(BDSFunction<T, ?>...columns) {
        List<BDSFunction<T, ?>> list = Arrays.asList(columns);
        return this.addSqlSelect(list);
    }
    public PhoenixLambdaQueryWrapper<T> addSqlSelect(boolean condition, BDSFunction<T, ?>...columns) {
        List<BDSFunction<T, ?>> list = Arrays.asList(columns);
        return this.addSqlSelect(condition, list);
    }

    public PhoenixLambdaQueryWrapper<T> addSqlSelect(List<BDSFunction<T, ?>> columns){
        return this.addSqlSelect(true, columns);
    }
    public PhoenixLambdaQueryWrapper<T> addSqlSelect(boolean condition, List<BDSFunction<T, ?>> columns){
        if(condition && !CollectionUtils.isEmpty(columns)) {
            for (BDSFunction<T, ?> column : columns) {
                this.addSqlSelect(condition, column);
            }
        }

        return (PhoenixLambdaQueryWrapper)this.typedThis;
    }
    public PhoenixLambdaQueryWrapper<T> addSqlSelect(boolean condition, BDSFunction<T, ?> column) {
        if(condition && Objects.nonNull(column)) {
            String strColumn = PhoenixLambdaUtil.getColumnName(column);
            this.sqlSelect.add(strColumn);
        }

        return (PhoenixLambdaQueryWrapper)this.typedThis;
    }

    @Override
    public List<String> getSqlSelect() {
        return this.sqlSelect;
    }

    @Override
    protected PhoenixLambdaQueryWrapper<T> instance() {
        return new PhoenixLambdaQueryWrapper(this.getEntity(), this.getEntityClass(), null, new MergeSegments(), "", "", "");
    }

    @Override
    public void clear() {
        if(Objects.nonNull(this.sqlSelect)) {
            this.sqlSelect.clear();
        }
        super.clear();
    }
}
