package com.hqwx.bigdata.client.phoenix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

/**
 * @Description: PhoenixManagerProperties
 * @Author: XuShengBin
 * @Date: 2022-09-02
 * @Ver: v1.0 -create
 */
@EnableConfigurationProperties(PhoenixManagerProperties.class)
@ConfigurationProperties(prefix = "hqconfig.big-data.phoenix")
public class PhoenixManagerProperties {
    private long checkTime = 1 * 60; //单位: 秒
    private List<PhoenixDataSource> dataSources;

    public long getCheckTime() {
        return checkTime;
    }
    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public List<PhoenixDataSource> getDataSources() {
        return dataSources;
    }
    public void setDataSources(List<PhoenixDataSource> dataSources) {
        this.dataSources = dataSources;
    }
}
