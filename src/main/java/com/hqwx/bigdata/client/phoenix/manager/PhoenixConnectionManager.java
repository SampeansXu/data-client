package com.hqwx.bigdata.client.phoenix.manager;

import com.hqwx.bigdata.client.phoenix.config.PhoenixDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: PhoenixConnectionManager
 * @Author: XuShengBin
 * @Date: 2022-09-02
 * @Ver: v1.0 -create
 */
public class PhoenixConnectionManager {
    protected static final Logger logger = LoggerFactory.getLogger(PhoenixConnectionManager.class);

    private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
    private PhoenixDataSource dataSource;
    private List<Connection> connectionPool = new ArrayList<>();
    private ReentrantLock mainLock = new ReentrantLock();
    private Integer lastIdx = 0;
    private boolean iskeepPoolRunning = false;

    public PhoenixConnectionManager(PhoenixDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean init() {
        this.mainLock.lock();
        {
            for (int i = 0; i < this.dataSource.getMaxPoolSize(); i++) {
                Connection connection = this.createConnection();
                try {
                    if (Objects.isNull(connection)
                            || connection.isClosed()) {
                        continue;
                    }
                    this.connectionPool.add(connection);
                } catch (Exception e) {
                }
            }

            Assert.isTrue(this.dataSource.getMaxPoolSize() == this.connectionPool.size(),
                    "DataSource init failed. DataSourceName:" + this.dataSource.getName());
        }
        this.mainLock.unlock();

//        //TODO 异步方式连接到Phoenix,进程启动时不建议,因为无法检测配置或Phoenix服务是否正常
//        this.keepConnectionPool(this.connectionPool.size());
        return true;
    }

    public void destroy() {
        this.mainLock.lock();
        {
            for (Connection connection : this.connectionPool) {
                try {
                    PhoenixConnectionHelper.closeConnection(connection);
                }catch (Exception e) {
                }
            }
            this.connectionPool.clear();
        }
        this.mainLock.unlock();

        if (Objects.nonNull(logger)) {
            logger.info("[BigData.PhoenixManager] DataSource:{} has been destroyed.", this.dataSource.getName());
        }
    }

    public void beatCheck() {
        boolean isChanged = false;
        this.mainLock.lock();
        {
            if(!this.connectionPool.isEmpty()) {
                Iterator<Connection> it = this.connectionPool.iterator();
                while (it.hasNext()) {
                    Connection connection = it.next();
                    try {
                        if (Objects.isNull(connection)
                                || connection.isClosed()) {
                            it.remove();
                            isChanged = true;
                            continue;
                        }

                        this.ping(connection);
                    } catch (Exception e) {
                    }
                }
            }else{ isChanged = true;}
        }
        this.mainLock.unlock();

        if(isChanged) {
            this.keepConnectionPool();
        }
    }

    private void closeStatement(Statement statement) {
        if (Objects.isNull(statement)) {
            return;
        }

        try {
            statement.close();
        } catch (SQLException e) {
        }
    }

    private void ping(Connection connection) {
        Statement statement = null;
        try {
            if (connection.isClosed()) {
                return;
            }
            statement = connection.createStatement();
            if (Objects.isNull(statement)) {
                return;
            }

            statement.setQueryTimeout(2);
            boolean bRet = statement.execute("SELECT 1");
            connection.commit();
        } catch (SQLException e) {
        } finally {
            this.closeStatement(statement);
        }
    }

    private Connection createConnection() {
        try {
//        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
//        Class.forName("org.apache.phoenix.queryserver.client.Driver");
            Class.forName(this.dataSource.getDriverClassName());
            return DriverManager.getConnection(this.dataSource.getJdbcUrl());
        } catch (Exception e) {
            if (Objects.nonNull(logger)) {
                logger.error("[BigData.PhoenixManager] DataSource:{} failed. Exception: {}",
                        this.dataSource.getName(), e.getMessage(), e);
            }
            return null;
        }
    }

    private void keepConnectionPool() {
        //验证连接池
        this.mainLock.lock();
        if(this.iskeepPoolRunning){
            this.mainLock.unlock();
            return;
        }
        if (this.connectionPool.size() >= this.dataSource.getMaxPoolSize()){
            this.mainLock.unlock();
            return;
        }
        this.mainLock.unlock();

        //执行连接池
        this.threadExecutor.execute(() -> {
            this.mainLock.lock();
            do{
                if(this.iskeepPoolRunning){
                    break;
                }
                this.iskeepPoolRunning = true;

                if (this.connectionPool.size() >= this.dataSource.getMaxPoolSize()){
                    break;
                }
                int begIdx = this.connectionPool.size();
                int succCount = 0, failedCount = 0;
                for (int i = begIdx; i < this.dataSource.getMaxPoolSize(); i++) {
                    Connection connection = this.createConnection();
                    try {
                        if (Objects.isNull(connection)
                                || connection.isClosed()) {
                            failedCount++;
                            continue;
                        }

                        succCount++;
                        this.connectionPool.add(connection);
                    } catch (Exception e) {
                    }
                }
                if (Objects.nonNull(logger) && failedCount > 0) {
                    logger.warn("[BigData.PhoenixManager] DataSource:{} keepConnectionPool has failed.Current ConnectionPool.size:{},MaxPoolSize:{},Succeed:{},Failed:{}",
                            this.dataSource.getName(), this.connectionPool.size(), this.dataSource.getMaxPoolSize(), succCount, failedCount);
                }
            }while(false);

            this.iskeepPoolRunning = false;
            this.mainLock.unlock();
        });
    }

    public Connection getConnection() {
        Connection result = null;
        boolean isChanged = false;
        this.mainLock.lock();
        {
            while (this.connectionPool.size() > 0) {
                try {
                    this.lastIdx = (this.lastIdx + 1) % this.connectionPool.size();
                    Connection connection = this.connectionPool.get(this.lastIdx);
                    if (Objects.isNull(connection) || connection.isClosed()) {
                        this.connectionPool.remove(connection);
                        isChanged = true;
                        continue;
                    }

                    result = connection;
                    break;
                } catch (Exception e) {
                    if (Objects.nonNull(logger)) {
                        logger.error("[BigData.PhoenixManager] DataSource:{} getConnection failed. Exception: {}",
                                this.dataSource.getName(), e.getMessage(), e);
                    }
                }
            }
        }
        this.mainLock.unlock();

        if(isChanged) {
            this.keepConnectionPool();
        }

        if (Objects.isNull(result)) {
            if (Objects.nonNull(logger)) {
                logger.error("[BigData.PhoenixManager] DataSource:{} getConnection failed. All connection are closed. lastIdx:{}",
                        this.dataSource.getName(), this.lastIdx);
            }
        }

        return result;
    }

    public PhoenixDataSource getDataSource() {
        return this.dataSource;
    }
}
