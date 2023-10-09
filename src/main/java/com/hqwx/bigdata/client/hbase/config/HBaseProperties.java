//package com.hqwx.bigdata.client.hbase.config;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//
//import java.util.Objects;
//
///**
// * @Description: HBase配置
// * @Author: XuShengBin
// * @Date: 2022-08-28
// * @Ver: v1.0 -create
// */
//@EnableConfigurationProperties(HBaseProperties.class)
//@ConfigurationProperties(prefix = "hqconfig.big-data.hbase")
//public class HBaseProperties {
//    private String quorum = "localhost";
//    private Integer clientPort = 2181;
//    private Integer operationTimeout = 5000;
//    private String hbaseMaster = null; //"localhost::16000"
//
//    private Configuration hbaseConfig = null;
//    public Configuration getHBaseConfig(){
//        if(Objects.nonNull(this.hbaseConfig)){
//            return this.hbaseConfig;
//        }
//
////        hbaseConfig.set("zookeeper.znode.parent", "/hbase");
////        hbaseConfig.set("hbase.zookeeper.quorum", "121.11.219.85,121.11.219.87");
////        hbaseConfig.set("hadoop.user.name", "root");
////        hbaseConfig.set("hbase.zookeeper.property.clientPort", "2181");
////        hbaseConfig.setInt("hbase.client.operation.timeout", 5*1000);
////        hbaseConfig.set("hbase.master","121.11.219.89:16000");
//
//        this.hbaseConfig = HBaseConfiguration.create();
//        this.hbaseConfig.set("hbase.zookeeper.quorum", this.quorum);
//        this.hbaseConfig.set("hbase.zookeeper.property.clientPort", this.clientPort.toString());
//        hbaseConfig.setInt("hbase.client.operation.timeout", this.operationTimeout);
//        if(Objects.nonNull(this.hbaseMaster)){
//            this.hbaseConfig.set("hbase.master", this.hbaseMaster);
//        }
//
//        return this.hbaseConfig;
//    }
//
//    public String getQuorum() {
//        return this.quorum;
//    }
//    public void setQuorum(String quorum) {
//        this.quorum = quorum;
//    }
//
//    public Integer getClientPort() {
//        return this.clientPort;
//    }
//    public void setClientPort(Integer clientPort) {
//        this.clientPort = clientPort;
//    }
//
//    public Integer getOperationTimeout() {
//        return operationTimeout;
//    }
//    public void setOperationTimeout(Integer operationTimeout) {
//        this.operationTimeout = operationTimeout;
//    }
//
//    public String getHbaseMaster() {
//        return this.hbaseMaster;
//    }
//    public void setHbaseMaster(String hbaseMaster) {
//        this.hbaseMaster = hbaseMaster;
//    }
//}
