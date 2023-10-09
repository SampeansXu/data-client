package com.hqwx.bigdata.client.mapper;

import com.hqwx.bigdata.client.phoenix.annotation.PhoenixMapper;
import com.hqwx.bigdata.client.phoenix.mapper.PhoenixBaseMapper;
import com.hqwx.bigdata.client.po.UserInfoPO;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

@PhoenixMapper(dataSourceName = "default")
public class UserInfoPhoenixMapper extends PhoenixBaseMapper<UserInfoPO> {
    @Value("")
    private String tableName;
    @PostConstruct
    private void init() {
        setTableName(this.tableName);
    }
}


