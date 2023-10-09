package com.hqwx.bigdata.client.phoenix.mapper;

import com.hqwx.bigdata.client.common.entity.EntityProperty;
import com.hqwx.bigdata.client.common.sql.constant.SqlConstant;
import com.hqwx.bigdata.client.common.sql.enums.BaseSQLKeyword;
import com.hqwx.bigdata.client.phoenix.enums.PhoenixSQLKeyword;
import com.hqwx.bigdata.client.phoenix.config.PhoenixDataSource;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixBaseException;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixConfigException;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixOperationException;
import com.hqwx.bigdata.client.phoenix.manager.PhoenixConnectionHelper;
import com.hqwx.bigdata.client.phoenix.manager.PhoenixConnectionManager;
import com.hqwx.bigdata.client.phoenix.parser.PhoenixEntityPropertiesParser;
import com.hqwx.bigdata.client.phoenix.parser.PhoenixSQLUtil;
import com.hqwx.bigdata.client.phoenix.utils.PhoenixReflectUtil;
import com.hqwx.bigdata.client.phoenix.utils.PhoenixTemplateUtil;
import com.hqwx.bigdata.client.phoenix.wrapper.PhoenixQueryWrapper;
import com.hqwx.bigdata.client.phoenix.wrapper.PhoenixWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: PhoenixOpMapper
 * @Author: XuShengBin
 * @Date: 2022-08-30
 * @Ver: v1.0 -create
 */
@SuppressWarnings(value = {"rawtypes", "unused"})
public class PhoenixOpMapper<T> {
    protected static final Logger logger = LoggerFactory.getLogger(PhoenixOpMapper.class);

    private Class<T> entityClass;

    private String dataSourceName;
    private String tableName = null;

    @SuppressWarnings("unchecked")
    public PhoenixOpMapper() {
        this.entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.dataSourceName = PhoenixReflectUtil.getDataSourceName(this.getClass());
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        if (StringUtils.isNotBlank(this.tableName)) {
            return this.tableName;
        }

        final String tableName = PhoenixReflectUtil.getTableName(this.entityClass);
        if (StringUtils.isBlank(tableName)) {
            throw new PhoenixOperationException("TableName is empty. Please set annotation PhoenixTable or TableName");
        }

        return tableName;
    }

    private PhoenixDataSource getDataSource() {
        PhoenixConnectionManager manager = PhoenixConnectionHelper.getConnectionManager(this.dataSourceName);
        return manager.getDataSource();
    }

    private boolean checkValid() {
        if (StringUtils.isBlank(this.dataSourceName)) {
            throw new PhoenixConfigException("DataSource is not exists. dataSourceName:" + this.dataSourceName);
        }

        return true;
    }

    private Connection getConnection(boolean isAutoCommit) throws PhoenixBaseException {
        if (!this.checkValid()) {
            return null;
        }
        Connection connection = PhoenixConnectionHelper.getConnection(this.dataSourceName);
        if (Objects.isNull(connection)) {
            throw new PhoenixOperationException("Connection is null. DataSourceName:" + this.dataSourceName);
        }

        try {
            connection.setAutoCommit(isAutoCommit);
        } catch (SQLException e) {
            if (Objects.nonNull(logger)) {
                logger.error("Connection setAutoCommit(%s) failed. DataSourceName:{} Exception:{}",
                        (isAutoCommit?"true":"false"), this.dataSourceName, e.getMessage(), e);
            }
        }

        return connection;
    }

    private void setQueryTimeout(Statement statement) throws SQLException {
        if (Objects.isNull(statement)) {
            return;
        }

        try {
            if (Objects.isNull(this.getDataSource().getValidationTimeout())) {
                return;
            }
            if (this.getDataSource().getValidationTimeout() <= 0) {
                return;
            }

            statement.setQueryTimeout(this.getDataSource().getValidationTimeout());
        } catch (Exception e) {

        }
    }

    private void closeStatement(Statement statement) {
        try {
            if (Objects.nonNull(statement)) {
                statement.close();
            }
        } catch (Exception e) {
            if (Objects.isNull(logger)) {
                logger.error("Failed to close Statement. Exception:{}", e.getMessage(), e);
            }
        }
    }

    private void setQueryTimeout(PreparedStatement preparedStatement) throws SQLException {
        if (Objects.isNull(preparedStatement)) {
            return;
        }

        try {
            if (Objects.isNull(this.getDataSource().getValidationTimeout())) {
                return;
            }
            if (this.getDataSource().getValidationTimeout() <= 0) {
                return;
            }

            preparedStatement.setQueryTimeout(this.getDataSource().getValidationTimeout());
        } catch (Exception e) {
        }
    }

    private void closeStatement(PreparedStatement statement) {
        try {
            if (Objects.nonNull(statement)) {
                statement.close();
            }
        } catch (Exception e) {
            if (Objects.isNull(logger)) {
                logger.error("Failed to close PreparedStatement. Exception:{}", e.getMessage(), e);
            }
        }
    }

    private void logSql(String sql) {
        if (Objects.isNull(logger) || StringUtils.isBlank(sql)) {
            return;
        }

        logger.debug("[BigData.SQL] phoenix sql: " + sql);
    }

    /**
     * 获取查询字段
     *
     * @param fields 指定字段名列表, 为空或null则获取全部
     * @return 查询完整字段名, 如: RowKey,BASE_INFO.ID,...,OTHER_INFO.MEMO
     */
    public String getSelectColumns(List<String> fields) {
        return PhoenixReflectUtil.getSelectByColumns(fields, this.entityClass);
    }

    /**
     * Phoenix执行Sql查询语句
     *
     * @param sql
     * @return
     */
    public List<T> selectBySQL(final String sql) {
        Connection connection = this.getConnection(false);

        List<T> resultList = null;
        Statement statement = null;
        try {
            this.logSql(sql);
            statement = connection.createStatement();
            this.setQueryTimeout(statement);
            ResultSet resultSet = statement.executeQuery(sql);
            connection.commit();
            resultList = PhoenixTemplateUtil.rowSetToBean(resultSet, entityClass);
        } catch (final Exception e) {
            throw new PhoenixOperationException("selectBySQL exception: " + e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return resultList;
    }
    public T selectOneBySQL(final String sql) {
        List<T> lst = this.selectBySQL(sql);
        if(CollectionUtils.isEmpty(lst)){
            return null;
        }

        return lst.get(0);
    }

    private String createSelectSQL(final PhoenixWrapper<T> wrapper){
        if(Objects.isNull(wrapper)){
            throw new PhoenixOperationException("wrapper is null");
        }

        StringBuilder sbSql = new StringBuilder();
        //first
        if(StringUtils.isNotBlank(wrapper.getFirst())){
            sbSql.append(wrapper.getFirst()).append(SqlConstant.Separator_Space);
        }

        //select
        String sqlSelect = this.getSelectColumns(wrapper.getSqlSelect());
        sbSql.append(BaseSQLKeyword.SELECT).append(SqlConstant.Separator_Space)
                .append(sqlSelect).append(SqlConstant.Separator_Space)
                .append(BaseSQLKeyword.FROM).append(SqlConstant.Separator_Space)
                .append(this.getTableName()).append(SqlConstant.Separator_Space);

        //where etc.
        String sqlWhere = wrapper.getCustomSqlSegment();
        if(StringUtils.isNotBlank(sqlWhere)) {
            sbSql.append(sqlWhere).append(SqlConstant.Separator_Space);
        }

        //last
        if(StringUtils.isNotBlank(wrapper.getLast())){
            sbSql.append(wrapper.getLast());
        }
        return sbSql.toString();
    }

    /**
     * 执行PhoenixQueryWrapper查询数据
     * @param wrapper PhoenixQueryWrapper
     * @return
     */
    public List<T> selectList(PhoenixWrapper<T> wrapper){
        String selectSQL = this.createSelectSQL(wrapper);
        return this.selectBySQL(selectSQL);
    }
    public T selectOne(PhoenixWrapper<T> wrapper){
        String selectSQL = this.createSelectSQL(wrapper);
        return this.selectOneBySQL(selectSQL);
    }

    /**
     * Phoenix执行增删改语句
     *
     * @param sql
     * @return
     */
    public boolean updateBySQL(final String sql) {
        Connection connection = this.getConnection(false);

        boolean result = false;
        Statement statement = null;
        try {
            this.logSql(sql);
            statement = connection.createStatement();
            this.setQueryTimeout(statement);
            int iRet = statement.executeUpdate(sql);
            connection.commit();
            result = iRet >= 0;
        } catch (final Exception e) {
            throw new PhoenixOperationException("updateBySQL exception: " + e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return result;
    }

    /**
     * Phoenix批量执行增删改语句
     * @param sqls: SQL语句列表
     * @return 对应序号SQL语句的执行结果
     */
    public List<Boolean> updateBatchBySQL(final List<String> sqls) {
        if (CollectionUtils.isEmpty(sqls)) {
            throw new PhoenixOperationException("sqls is empty");
        }

        Connection connection = this.getConnection(false);
        List<Boolean> resultList = new ArrayList<Boolean>();
        Statement statement = null;
        try {
            StringBuilder sbSQL = new StringBuilder();
            statement = connection.createStatement();
            for (String sql : sqls) {
                statement.addBatch(sql);
                sbSQL.append(sql+"\n");
            }
            this.logSql(sbSQL.toString());
            this.setQueryTimeout(statement);
            int[] iRets = statement.executeBatch();
            connection.commit();
            if (Objects.nonNull(iRets)) {
                for (int iRet : iRets) {
                    resultList.add(iRet >= 0);
                }
            }
        } catch (final Exception e) {
            throw new PhoenixOperationException("updateBatchBySQL exception: " + e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return resultList;
    }

    private String getSaveSQL(T entity) {
        final String tableName = this.getTableName();
        PhoenixEntityPropertiesParser<T> parser = new PhoenixEntityPropertiesParser<T>(entity);
        Map<String, EntityProperty> field2PropertyMap = parser.getField2PropertyMap();
        String columnsList = "";
        String valuesList = "";
        for (EntityProperty property : field2PropertyMap.values()) {
            if (Objects.isNull(property.getValue())) {
                continue;
            }

            if (StringUtils.isNotBlank(columnsList)) {
                columnsList += SqlConstant.Separator_Comma + " ";
            }
            columnsList += property.getFullColumnName();

            if (StringUtils.isNotBlank(valuesList)) {
                valuesList += SqlConstant.Separator_Comma + " ";
            }
            valuesList += PhoenixSQLUtil.property2SQLStr(property.getValue());
        }

        String sqlSave = PhoenixSQLKeyword.UPSERT_INTO + " " + tableName +
                " (" + columnsList + ")" +
                " VALUES(" + valuesList + ")";
        return sqlSave;
    }

    /**
     * 存储实例对象
     * @param entity 实例对象
     * @return 执行结果
     */
    public boolean save(T entity) {
        Connection connection = this.getConnection(false);

        boolean result = false;
        Statement statement = null;
        try {
            String saveSQL = this.getSaveSQL(entity);
            this.logSql(saveSQL);
            statement = connection.createStatement();
            this.setQueryTimeout(statement);
            int iRet = statement.executeUpdate(saveSQL);
            connection.commit();
            result = iRet >= 0;
        } catch (final Exception e) {
            throw new PhoenixOperationException("save exception: " + e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return result;
    }

    /**
     * 批量存储实例对象
     * @param entities 实例对象列表
     * @return 对应序号SQL语句的执行结果
     */
    public List<Boolean> saveBatch(List<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            throw new PhoenixOperationException("entities is empty");
        }
        Connection connection = this.getConnection(false);

        List<Boolean> resultList = new ArrayList<Boolean>();
        Statement statement = null;
        try {
            StringBuilder sbSQL = new StringBuilder();
            statement = connection.createStatement();
            for (T entity : entities) {
                String saveSQL = this.getSaveSQL(entity);
                statement.addBatch(saveSQL);
                sbSQL.append(saveSQL+"; ");
            }
            this.logSql(sbSQL.toString());
            this.setQueryTimeout(statement);
            int[] iRets = statement.executeBatch();
            connection.commit();
            if (Objects.nonNull(iRets)) {
                for (int iRet : iRets) {
                    resultList.add(iRet >= 0);
                }
            }
        } catch (final Exception e) {
            throw new PhoenixOperationException("saveBatch exception: " + e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return resultList;
    }


    private String createDeleteSQL(final PhoenixWrapper<T> wrapper){
        if(Objects.isNull(wrapper)){
            throw new PhoenixOperationException("wrapper is null");
        }

        StringBuilder sbSql = new StringBuilder();
        //first
        if(StringUtils.isNotBlank(wrapper.getFirst())){
            sbSql.append(wrapper.getFirst()).append(SqlConstant.Separator_Space);
        }

        //delete
        sbSql.append(BaseSQLKeyword.DELETE).append(SqlConstant.Separator_Space)
                .append(BaseSQLKeyword.FROM).append(SqlConstant.Separator_Space)
                .append(this.getTableName()).append(SqlConstant.Separator_Space);

        //where etc.
        String sqlWhere = wrapper.getCustomSqlSegment();
        if(StringUtils.isNotBlank(sqlWhere)) {
            sbSql.append(sqlWhere).append(SqlConstant.Separator_Space);
        }

        //last
        if(StringUtils.isNotBlank(wrapper.getLast())){
            sbSql.append(wrapper.getLast());
        }
        return sbSql.toString();
    }

    /**
     * 删除数据
     * @param wrapper PhoenixQueryWrapper
     * @return
     */
    public boolean delete(PhoenixQueryWrapper<T> wrapper){
        Connection connection = this.getConnection(false);

        boolean result = false;
        Statement statement = null;
        try {
            String sql = this.createDeleteSQL(wrapper);
            this.logSql(sql);
            statement = connection.createStatement();
            this.setQueryTimeout(statement);
            int iRet = statement.executeUpdate(sql);
            connection.commit();
            result = iRet >= 0;
        } catch (final Exception e) {
            throw new PhoenixOperationException("delete exception: " + e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return result;
    }
}
