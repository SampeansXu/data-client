package com.hqwx.bigdata.client.phoenix.wrapper;

import com.hqwx.bigdata.client.common.lambda.BDSFunction;
import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;
import com.hqwx.bigdata.client.common.sql.enums.SqlLike;
import com.hqwx.bigdata.client.common.sql.interfaces.Func;
import com.hqwx.bigdata.client.common.sql.interfaces.Join;
import com.hqwx.bigdata.client.common.sql.interfaces.Nested;
import com.hqwx.bigdata.client.common.sql.interfaces.Transfer;
import com.hqwx.bigdata.client.common.sql.segments.ISqlSegment;
import com.hqwx.bigdata.client.common.sql.segments.MergeSegments;
import com.hqwx.bigdata.client.common.sql.utils.SqlUtil;
import com.hqwx.bigdata.client.common.utils.SerializationUtil;
import com.hqwx.bigdata.client.phoenix.enums.PhoenixWrapperKeyword;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixOperationException;
import com.hqwx.bigdata.client.phoenix.lambda.PhoenixLambdaUtil;
import com.hqwx.bigdata.client.phoenix.parser.PhoenixSQLUtil;
import com.hqwx.bigdata.client.phoenix.utils.PhoenixReflectUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Description: PhoenixBaseWrapper
 * @Author: XuShengBin
 * @Date: 2022-09-01
 * @Ver: v1.0 -create
 */
public abstract class PhoenixBaseWrapper<T, R, Children extends PhoenixBaseWrapper<T, R, Children>> extends PhoenixWrapper<T> implements Transfer<Children, R>, Nested<Children, Children>, Join<Children>, Func<Children, R> {
    protected final Children typedThis = (Children) this;
    protected String lastSql;
    protected String sqlComment;
    protected String sqlFirst;
    private T entity;
    protected MergeSegments expression;
    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public PhoenixBaseWrapper(){
    }

    @Override
    public String getLast() {
        return this.lastSql;
    }
    @Override
    public String getFirst() {
        return this.sqlFirst;
    }

    @Override
    public T getEntity() {
        return this.entity;
    }

    public Children setEntity(T entity) {
        this.entity = entity;
        return this.typedThis;
    }

    public Class<T> getEntityClass() {
        if (this.entityClass == null && this.entity != null) {
            this.entityClass = (Class<T>) this.entity.getClass();
        }

        return this.entityClass;
    }

    public Children setEntityClass(Class<T> entityClass) {
        if (entityClass != null) {
            this.entityClass = entityClass;
        }

        return this.typedThis;
    }

    @Override
    public Children eq(boolean condition, R column, Object val) {
        return this.addCondition(condition, column, BaseSQLKeyword.EQ, val);
    }

    @Override
    public Children ne(boolean condition, R column, Object val) {
        return this.addCondition(condition, column, BaseSQLKeyword.NE, val);
    }

    @Override
    public Children gt(boolean condition, R column, Object val) {
        return this.addCondition(condition, column, BaseSQLKeyword.GT, val);
    }

    @Override
    public Children ge(boolean condition, R column, Object val) {
        return this.addCondition(condition, column, BaseSQLKeyword.GE, val);
    }

    @Override
    public Children lt(boolean condition, R column, Object val) {
        return this.addCondition(condition, column, BaseSQLKeyword.LT, val);
    }

    @Override
    public Children le(boolean condition, R column, Object val) {
        return this.addCondition(condition, column, BaseSQLKeyword.LE, val);
    }

    @Override
    public Children like(boolean condition, R column, Object val) {
        return this.likeValue(condition, BaseSQLKeyword.LIKE, column, val, SqlLike.DEFAULT);
    }

    @Override
    public Children likeLeft(boolean condition, R column, Object val) {
        return this.likeValue(condition, BaseSQLKeyword.LIKE, column, val, SqlLike.LEFT);
    }

    @Override
    public Children likeRight(boolean condition, R column, Object val) {
        return this.likeValue(condition, BaseSQLKeyword.LIKE, column, val, SqlLike.RIGHT);
    }

    @Override
    public Children between(boolean condition, R column, Object val1, Object val2) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.BETWEEN, () -> {
                return this.formatParam(val1);
            }, BaseSQLKeyword.AND, () -> {
                return this.formatParam(val2);
            });
        });
    }

    @Override
    public Children and(boolean condition, Consumer<Children> consumer) {
        return this.and(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children or(boolean condition, Consumer<Children> consumer) {
        return this.or(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children nested(boolean condition, Consumer<Children> consumer) {
        return this.addNestedCondition(condition, consumer);
    }

    @Override
    public Children not(boolean condition, Consumer<Children> consumer) {
        return this.not(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children or(boolean condition) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.OR);
        });
    }

    @Override
    public Children apply(boolean condition, String applySql, Object... values) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(PhoenixWrapperKeyword.APPLY, () -> {
                return this.formatSqlMaybeWithParam(applySql, (String)null, values);
            });
        });
    }

    @Override
    public Children last(boolean condition, String lastSql) {
        if (condition) {
            this.lastSql = " " + lastSql;
        }

        return this.typedThis;
    }

    @Override
    public Children comment(boolean condition, String comment) {
        if (condition) {
            this.sqlComment = comment;
        }

        return this.typedThis;
    }

    @Override
    public Children first(boolean condition, String firstSql) {
        if (condition) {
            this.sqlFirst = firstSql;
        }

        return this.typedThis;
    }

    @Override
    public Children exists(boolean condition, String existsSql, Object... values) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.EXISTS, () -> {
                return String.format("(%s)", this.formatSqlMaybeWithParam(existsSql, (String)null, values));
            });
        });
    }

    @Override
    public Children notExists(boolean condition, String existsSql, Object... values) {
        return this.not(condition).exists(condition, existsSql, values);
    }

    @Override
    public Children isNull(boolean condition, R column) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.IS_NULL);
        });
    }

    @Override
    public Children isNotNull(boolean condition, R column) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.IS_NOT_NULL);
        });
    }

    @Override
    public Children in(boolean condition, R column, Collection<?> coll) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.IN, this.inExpression(coll));
        });
    }

    @Override
    public Children in(boolean condition, R column, Object... values) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.IN, this.inExpression(values));
        });
    }

    @Override
    public Children notIn(boolean condition, R column, Collection<?> coll) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.NOT_IN, this.inExpression(coll));
        });
    }

    @Override
    public Children notIn(boolean condition, R column, Object... values) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.NOT_IN, this.inExpression(values));
        });
    }

    @Override
    public Children inSql(boolean condition, R column, String inValue) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.IN, () -> {
                return String.format("(%s)", inValue);
            });
        });
    }

    @Override
    public Children gtSql(boolean condition, R column, String inValue) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.GT, () -> {
                return String.format("(%s)", inValue);
            });
        });
    }

    @Override
    public Children geSql(boolean condition, R column, String inValue) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.GE, () -> {
                return String.format("(%s)", inValue);
            });
        });
    }

    @Override
    public Children ltSql(boolean condition, R column, String inValue) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.LT, () -> {
                return String.format("(%s)", inValue);
            });
        });
    }

    @Override
    public Children leSql(boolean condition, R column, String inValue) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.LE, () -> {
                return String.format("(%s)", inValue);
            });
        });
    }

    @Override
    public Children notInSql(boolean condition, R column, String inValue) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), BaseSQLKeyword.NOT_IN, () -> {
                return String.format("(%s)", inValue);
            });
        });
    }

    @Override
    public Children groupBy(boolean condition, R column, R... columns) {
        return this.maybeDo(condition, () -> {
            String one = this.columnToString(column);
            if (ArrayUtils.isNotEmpty(columns)) {
                one = one + "," + this.columnsToString(columns);
            }

            String finalOne = one;
            this.appendSqlSegments(BaseSQLKeyword.GROUP_BY, () -> {
                return finalOne;
            });
        });
    }

    @Override
    @SafeVarargs
    public final Children orderBy(boolean condition, boolean isAsc, R column, R... columns) {
        return this.maybeDo(condition, () -> {
            BaseSQLKeyword mode = isAsc ? BaseSQLKeyword.ASC : BaseSQLKeyword.DESC;
            this.appendSqlSegments(BaseSQLKeyword.ORDER_BY, this.columnToSqlSegment(this.columnSqlInjectFilter(column)), mode);
            if (ArrayUtils.isNotEmpty(columns)) {
                Arrays.stream(columns).forEach((c) -> {
                    this.appendSqlSegments(BaseSQLKeyword.ORDER_BY, this.columnToSqlSegment(this.columnSqlInjectFilter(c)), mode);
                });
            }

        });
    }

    @Override
    public Children groupBy(boolean condition, R column) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.GROUP_BY, () -> {
                return this.columnToString(column);
            });
        });
    }

    @Override
    public Children groupBy(boolean condition, List<R> columns) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.GROUP_BY, () -> {
                return this.columnsToString(columns);
            });
        });
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R column) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.ORDER_BY, this.columnToSqlSegment(this.columnSqlInjectFilter(column)), isAsc ? BaseSQLKeyword.ASC : BaseSQLKeyword.DESC);
        });
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, List<R> columns) {
        return this.maybeDo(condition, () -> {
            columns.forEach((c) -> {
                this.appendSqlSegments(BaseSQLKeyword.ORDER_BY, this.columnToSqlSegment(this.columnSqlInjectFilter(c)), isAsc ? BaseSQLKeyword.ASC : BaseSQLKeyword.DESC);
            });
        });
    }

    protected R columnSqlInjectFilter(R column) {
        return column;
    }

    @Override
    public Children having(boolean condition, String sqlHaving, Object... params) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.HAVING, () -> {
                return this.formatSqlMaybeWithParam(sqlHaving, (String)null, params);
            });
        });
    }

    @Override
    public Children limit(boolean condition, Integer from, int rows) {
        return this.maybeDo(condition, () -> {
            this.setLimit(from, rows);
        });
    }

    @Override
    public Children func(boolean condition, Consumer<Children> consumer) {
        return this.maybeDo(condition, () -> {
            consumer.accept(this.typedThis);
        });
    }

    protected Children not(boolean condition) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.NOT);
        });
    }

    protected Children and(boolean condition) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(BaseSQLKeyword.AND);
        });
    }

    protected Children likeValue(boolean condition, BaseSQLKeyword keyword, R column, Object val, SqlLike sqlLike) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), keyword, () -> {
                return this.formatParam(SqlUtil.concatLike(val, sqlLike));
            });
        });
    }

    protected Children addCondition(boolean condition, R column, BaseSQLKeyword PhoenixSQLKeyword, Object val) {
        return this.maybeDo(condition, () -> {
            this.appendSqlSegments(this.columnToSqlSegment(column), PhoenixSQLKeyword, () -> {
                return this.formatParam(val);
            });
        });
    }

    protected Children addNestedCondition(boolean condition, Consumer<Children> consumer) {
        return this.maybeDo(condition, () -> {
            Children instance = this.instance();
            consumer.accept(instance);
            this.appendSqlSegments(PhoenixWrapperKeyword.APPLY, (ISqlSegment) instance);
        });
    }

    protected abstract Children instance();

    protected final String formatSqlMaybeWithParam(String sqlStr, Object... params) {
        if (StringUtils.isBlank(sqlStr)) {
            return null;
        } else {
            if (ArrayUtils.isNotEmpty(params)) {
                for(int i = 0; i < params.length; ++i) {
                    String target = "{" + i + "}";
                    sqlStr = sqlStr.replace(target, this.formatParam(params[i]));
                }
            }

            return sqlStr;
        }
    }

    protected final String formatParam(Object param) {
        return PhoenixSQLUtil.property2SQLStr(param);
    }

    protected final Children maybeDo(boolean condition, PhoenixBaseWrapper.DoSomething something) {
        if (condition) {
            something.doIt();
        }

        return this.typedThis;
    }

    protected ISqlSegment inExpression(Collection<?> value) {
        return CollectionUtils.isEmpty(value) ? () -> {
            return "()";
        } : () -> {
            return (String)value.stream().map((val) -> {
                return this.formatParam(val);
            }).collect(Collectors.joining(",", "(", ")"));
        };
    }

    protected ISqlSegment inExpression(Object[] values) {
        return ArrayUtils.isEmpty(values) ? () -> {
            return "()";
        } : () -> {
            return (String)Arrays.stream(values).map((val) -> {
                return this.formatParam(val);
            }).collect(Collectors.joining(",", "(", ")"));
        };
    }

    protected void initNeed() {
        this.expression = new MergeSegments();
        this.lastSql = "";
        this.sqlComment = "";
        this.sqlFirst = "";
    }

    @Override
    public void clear() {
        this.entity = null;
        this.expression.clear();
        this.lastSql = "";
        this.sqlComment = "";
        this.sqlFirst = "";
    }

    protected void appendSqlSegments(ISqlSegment... sqlSegments) {
        this.expression.add(sqlSegments);
    }
    protected void setLimit(Integer from, int rows){
        this.expression.setLimit(from, rows);
    }

    @Override
    public String getSqlSegment() {
        return this.expression.getSqlSegment() + this.lastSql;
    }

    @Override
    public MergeSegments getExpression() {
        return this.expression;
    }

    protected final ISqlSegment columnToSqlSegment(R column) {
        return () -> {
            return this.columnToString(column);
        };
    }

    protected String columnToString(R column) {
        String fileName = "";
        if (column instanceof BDSFunction){
            fileName = PhoenixLambdaUtil.getColumnName(column);
        }else if(column instanceof String) {
            fileName = ((String)column);
        }else {
            throw new PhoenixOperationException("column is invalid");
        }

        String columnName = fileName;
        Class<T> entityClass = this.getEntityClass();
        if(Objects.nonNull(entityClass)) {
            columnName = PhoenixReflectUtil.getFullColumnName(fileName, entityClass);
            if(StringUtils.isBlank(columnName)){
                //有没可能没有设置注解
                columnName = fileName;
            }
        }
        if(StringUtils.isBlank(columnName)){
            throw new PhoenixOperationException("column name is not existent");
        }

        return columnName;
    }

    protected String columnsToString(R... columns) {
        return (String)Arrays.stream(columns).map(this::columnToString).collect(Collectors.joining(","));
    }

    protected String columnsToString(List<R> columns) {
        return (String)columns.stream().map(this::columnToString).collect(Collectors.joining(","));
    }

    @Override
    public Children clone() {
        return (Children) SerializationUtil.clone(this.typedThis);
    }

    @FunctionalInterface
    public interface DoSomething {
        void doIt();
    }
}
