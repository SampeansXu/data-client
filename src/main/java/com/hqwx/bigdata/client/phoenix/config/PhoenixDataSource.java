package com.hqwx.bigdata.client.phoenix.config;

import com.hqwx.bigdata.client.phoenix.constant.PhoenixConstant;
import org.springframework.util.Assert;

/**
 * @Description: Phoenix配置
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
public class PhoenixDataSource {
    private String name = PhoenixConstant.DataSource_Name_Default;
//    private String jdbcUrl = "jdbc:phoenix:121.11.219.89,121.11.219.89:8765";
//    private String driverClassName = "org.apache.phoenix.queryserver.client.Driver";
    private String jdbcUrl = "jdbc:phoenix:localhost:8765";
    private String driverClassName = "org.apache.phoenix.queryserver.jdbc.PhoenixDriver";
    private Integer validationTimeout = 5*1000;
    private Integer maxPoolSize = 5;

    public PhoenixDataSource() {
    }

    public void setConfig(final PhoenixDataSource config) {
        Assert.notNull(config, "config is null");
        this.name = config.getName();
        this.jdbcUrl = config.getJdbcUrl();
        this.driverClassName = config.getDriverClassName();
        this.validationTimeout = config.getValidationTimeout();
        this.maxPoolSize = config.getMaxPoolSize();
    }

    public boolean isValid() {
        Assert.hasText(this.name, "DataSource Property name is null");
        Assert.hasText(this.jdbcUrl, "DataSource Property jdbcUrl is null");
        Assert.hasText(this.driverClassName, "DataSource Property driverClassName is null");
        Assert.notNull(this.validationTimeout, "DataSource Property validationTimeout is null");
        Assert.notNull(this.maxPoolSize, "DataSource Property maxPoolSize is null");
        return true;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverClassName() {
        return driverClassName;
    }
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Integer getValidationTimeout() {
        return validationTimeout;
    }
    public void setValidationTimeout(Integer validationTimeout) {
        this.validationTimeout = validationTimeout;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }
    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
