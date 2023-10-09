package com.hqwx.bigdata.client.phoenix.wrapper;

import com.hqwx.bigdata.client.common.sql.segments.MergeSegments;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Description: PhoenixWrapper
 * @Author: XuShengBin
 * @Date: 2022-09-07
 * @Ver: v1.0 -create
 */
public class PhoenixQueryWrapper<T> extends PhoenixBaseWrapper<T, String, PhoenixQueryWrapper<T>> {
    private List<String> sqlSelect;

//    public PhoenixQueryWrapper() {
//        this(null);
//    }

    public PhoenixQueryWrapper(Class<T> entityClass) {
        this.sqlSelect = new ArrayList<>();
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    private PhoenixQueryWrapper(T entity, Class<T> entityClass, List<String> sqlSelect, MergeSegments mergeSegments, String lastSql, String sqlComment, String sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.sqlSelect = sqlSelect;
        this.expression = mergeSegments;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    public PhoenixQueryWrapper<T> addSqlSelect(String...columns){
        List<String> list = Arrays.asList(columns);
        return this.addSqlSelect(list);
    }
    public PhoenixQueryWrapper<T> addSqlSelect(boolean condition, String...columns) {
        List<String> list = Arrays.asList(columns);
        return this.addSqlSelect(condition, list);
    }

    public PhoenixQueryWrapper<T> addSqlSelect(List<String> columns){
        return this.addSqlSelect(true, columns);
    }
    public PhoenixQueryWrapper<T> addSqlSelect(boolean condition, List<String> columns){
        return (PhoenixQueryWrapper)this.maybeDo(condition, () -> {
            if(CollectionUtils.isEmpty(columns)){
                return;
            }

            if(Objects.isNull(this.sqlSelect)){
                this.sqlSelect = new ArrayList<>();
            }
            this.sqlSelect.addAll(columns);
        });
    }

    @Override
    public List<String> getSqlSelect() {
        return this.sqlSelect;
    }

    @Override
    protected PhoenixQueryWrapper<T> instance() {
        return new PhoenixQueryWrapper(this.getEntity(), this.getEntityClass(), null, new MergeSegments(), "", "", "");
    }

    @Override
    public void clear() {
        if(Objects.nonNull(this.sqlSelect)) {
            this.sqlSelect.clear();
        }
        super.clear();
    }
}
