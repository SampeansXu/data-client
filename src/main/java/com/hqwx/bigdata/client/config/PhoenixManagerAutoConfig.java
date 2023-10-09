package com.hqwx.bigdata.client.config;

import com.google.common.collect.Lists;
import com.hqwx.bigdata.client.phoenix.config.PhoenixDataSource;
import com.hqwx.bigdata.client.phoenix.config.PhoenixManagerProperties;
import com.hqwx.bigdata.client.phoenix.manager.PhoenixManager;
import org.springframework.context.annotation.Bean;

/**
 * @Description: PhoenixManagerAutoConfig
 * @Author: XuShengBin
 * @Date: 2022-09-05
 * @Ver: v1.0 -create
 */
//@Configuration
public class PhoenixManagerAutoConfig {
    @Bean
    public PhoenixManagerProperties phoenixConfig() {
//        return  new PhoenixManagerProperties();
        String jdbcUrl = "jdbc:phoenix:thin:url=http://121.11.219.89:8765;serialization=PROTOBUF";
        String driverClassName = "org.apache.phoenix.queryserver.client.Driver";
        PhoenixManagerProperties phoenixConfig = new PhoenixManagerProperties();
        PhoenixDataSource phoenixDS = new PhoenixDataSource();
        phoenixDS.setJdbcUrl(jdbcUrl);
        phoenixDS.setDriverClassName(driverClassName);
        phoenixConfig.setDataSources(Lists.newArrayList());
        phoenixConfig.getDataSources().add(phoenixDS);
        return phoenixConfig;
    }

    @Bean
    public PhoenixManager phoenixManager(PhoenixManagerProperties phoenixConfig) {
        PhoenixManager phoenixManager = new PhoenixManager();
        phoenixManager.init(phoenixConfig);
        return phoenixManager;
    }
}
