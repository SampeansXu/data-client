package com.hqwx.bigdata.client.phoenix.manager;


import com.hqwx.bigdata.client.phoenix.config.PhoenixDataSource;
import com.hqwx.bigdata.client.phoenix.config.PhoenixManagerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: PhoenixManager
 * @Author: XuShengBin
 * @Date: 2022-09-02
 * @Ver: v1.0 -create
 */
public class PhoenixManager {
    protected static final Logger logger = LoggerFactory.getLogger(PhoenixManager.class);

    private ScheduledExecutorService threadExecutor = Executors.newSingleThreadScheduledExecutor();
    private long checkTime = 1 * 60; //单位:秒

    private Map<String, PhoenixConnectionManager> managerMap = new HashMap<String, PhoenixConnectionManager>();
    private ReentrantLock managerLock = new ReentrantLock();

    @PreDestroy
    private void destroy() {
        this.threadExecutor.shutdown();
        this.shutdown();
        if (Objects.nonNull(logger)) {
            logger.info("[BigData.PhoenixManager] destroyed");
        }
    }

    public boolean init(PhoenixManagerProperties config) {
        Assert.notNull(config, "[hqconfig.big-data.phoenix] is not config");
        Assert.notEmpty(config.getDataSources(), "[hqconfig.big-data.phoenix.dataSources] is not config");
        if(Objects.nonNull(config.getCheckTime()) && config.getCheckTime() > 0){
            this.checkTime = config.getCheckTime();
        }
        this.managerLock.lock();
        {
            for (PhoenixDataSource dataSource : config.getDataSources()) {
                Assert.isTrue(!this.managerMap.containsKey(dataSource.getName()), "Config DataSource is repeated. DataSources.Name:" + dataSource.getName());
                Assert.isTrue(dataSource.isValid(),"DataSource is valid. DataSourceName:" + dataSource.getName());

                PhoenixConnectionManager manager = new PhoenixConnectionManager(dataSource);
                this.managerMap.put(dataSource.getName(), manager);
                Assert.isTrue(manager.init(), "DataSource init failed, Check config of DataSources.Name:" + dataSource.getName());
            }
            PhoenixConnectionHelper.setManagerMap(managerMap);
        }
        this.managerLock.unlock();

        this.start();
        return true;
    }


    private void printDetail() {
        StringBuilder sb = new StringBuilder();
        final String LineSeparator = "──────────────────────────────────────────────────────────────────────────────────" + System.lineSeparator();
        sb.append(System.lineSeparator());
        sb.append("┌" + LineSeparator);
        sb.append("│ PhoenixManager Info: checkTime:" + this.checkTime + "s" + System.lineSeparator());
        {
            sb.append("├" + LineSeparator);
            sb.append("│ DataSource Info:" + System.lineSeparator());
            int idx = 1;
            this.managerLock.lock();
            for (PhoenixConnectionManager manager : this.managerMap.values()) {
                PhoenixDataSource config = manager.getDataSource();
                sb.append("│ " + (idx++) + ".")
                        .append("Name:" + config.getName())
                        .append(", MaxPoolSize:" + config.getMaxPoolSize())
                        .append(", ValidationTimeout:" + config.getValidationTimeout() + "s")
                        .append(System.lineSeparator());
            }
            this.managerLock.unlock();
        }
        sb.append("└" + LineSeparator);

        System.out.println(sb.toString());
        if (Objects.nonNull(logger)) {
            logger.info(sb.toString());
        }
    }

    private void beatCheck() {
        this.managerLock.lock();
        {
            for (PhoenixConnectionManager manager : this.managerMap.values()) {
                manager.beatCheck();
            }
        }
        this.managerLock.unlock();
    }

    private void start() {
        this.threadExecutor.scheduleAtFixedRate(() -> {
            //定时任务
            beatCheck();
        }, this.checkTime, this.checkTime, TimeUnit.SECONDS);

        printDetail();
        System.out.println("[BigData.PhoenixManager] started");
        if (Objects.nonNull(logger)) {
            logger.info("[BigData.PhoenixManager] started");
        }
    }

    private void shutdown() {
        this.managerLock.lock();
        {
            for (PhoenixConnectionManager manager : this.managerMap.values()) {
                manager.destroy();
            }
            this.managerMap.clear();
        }
        this.managerLock.unlock();
    }
}
