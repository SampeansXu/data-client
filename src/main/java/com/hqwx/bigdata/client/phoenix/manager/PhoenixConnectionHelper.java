package com.hqwx.bigdata.client.phoenix.manager;

import com.hqwx.bigdata.client.phoenix.exception.PhoenixBaseException;
import com.hqwx.bigdata.client.phoenix.exception.PhoenixManagerException;

import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: PhoenixConnectionHelper
 * @Author: XuShengBin
 * @Date: 2022-08-30
 * @Ver: v1.0 -create
 */
public class PhoenixConnectionHelper {

    private static Map<String, PhoenixConnectionManager> managerMap = null;

    public static void setManagerMap(Map<String, PhoenixConnectionManager> mngMap){
        managerMap = mngMap;
    }

    public static PhoenixConnectionManager getConnectionManager(final String dataSourceName){
        try {
            return managerMap.get(dataSourceName);
        }catch (Exception e) {
            throw new PhoenixManagerException("getConnectionManager failed. Exception: " + e.getMessage());
        }
    }
    public static Connection getConnection(final String dataSourceName) throws PhoenixBaseException {
        try {
            PhoenixConnectionManager manager = getConnectionManager(dataSourceName);
            return manager.getConnection();
        }catch (Exception e) {
            throw new PhoenixManagerException("getConnection failed. Exception: " + e.getMessage());
        }
    }

    public static void closeConnection(Connection connection) throws PhoenixBaseException{
        try {
            if(Objects.isNull(connection)){
                return;
            }
            connection.close();
        } catch (Exception e) {
            throw new PhoenixManagerException("closeConnection failed. Exception: " + e.getMessage());
        }
    }
}
