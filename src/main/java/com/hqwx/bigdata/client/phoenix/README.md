# 使用说明
## 1.pom.xml引入
```
<dependency>
  <groupId>com.hqwx</groupId>
  <artifactId>bigdata-client</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```
## 2.项目中引入
### 2.1项目配置文件 yml格式
``` editorconfig
hqconfig: 
  big-data:
    phoenix:
      checkTime: 60  #定时检测时间,单位:秒  
      dataSources:
        - name: userStudyLog  #data source名称
          jdbcUrl: jdbc:phoenix:thin:url=http://121.11.219.89:8765;serialization=PROTOBUF
          driverClassName: org.apache.phoenix.queryserver.client.Driver
          maxPoolSize: 100       #Phoenix连接数
          validationTimeout: 10  #等待Phoenix结果超时,单位:秒
        - name: userBookInfo  #data source名称
          jdbcUrl: jdbc:phoenix:thin:url=http://localhost:8765;serialization=PROTOBUF
          driverClassName: org.apache.phoenix.queryserver.client.Driver
          maxPoolSize: 300
          validationTimeout: 20
```
### 2.2项目配置类
```java
@Configuration
public class PhoenixManagerAutoConfig {
    @Bean
    public PhoenixManagerProperties phoenixConfig() {
        return  new PhoenixManagerProperties();
    }

    @Bean
    public PhoenixManager phoenixManager(PhoenixManagerProperties phoenixConfig) {
        PhoenixManager phoenixManager = new PhoenixManager();
        phoenixManager.init(phoenixConfig);
        return phoenixManager;
    }
}
```
### 2.3业务使用
#### 2.3.1 po对象定义
```java
@Data
@PhoenixTable(type = PhoenixTable.Type.View, name = "TEST_BOOK_STUDY_LOG_VIEW(表或视图名)")
public class UserInfoPO implements Serializable {
    @PhoenixColumn(name = "LOG_ROWKEY")
    private String logRowKey;

    @PhoenixColumn(name = "UID 字段名", family = "BASE_INFO 列族名")
    private Long uid;//用户uid

    @PhoenixColumn(name = "GOODS_NAME", family = "BASE_INFO")
    private Long goodsName; //商品名称

    @PhoenixColumn(name = "OBJ_ID", family = "BASE_INFO")
    private Long objId; //"产品资源id"

    @PhoenixColumn(name = "OBJ_TYPE", family = "BASE_INFO")
    private Short objType; //"产品资源类型:音视频、直播、电子书"

    @PhoenixColumn(name = "OBJ_NAME", family = "BASE_INFO")
    private Long objName; //"产品资源名称"

    @PhoenixColumn(name = "CREATE_DATE", family = "BASE_INFO")
    private Date createDate; //"创建时间"

    @PhoenixColumn(name = "UPDATE_DATE", family = "BASE_INFO")
    private Date updateDate; //"更新时间"

    @PhoenixColumn(name = "STUDY_COUNT", family = "RESOURCE_INFO")
    private Integer studyCount; //"学习量,如书的阅读字数"

    @PhoenixColumn(name = "RESOURCE_TOTAL", family = "RESOURCE_INFO")
    private Integer resourceTotal; //"资源总量,如书的总字数"
}
```
#### 2.3.2 mapper定义
```java
@PhoenixMapper(dataSourceName = "default 资源配置名")
public class UserInfoPhoenixMapper extends PhoenixBaseMapper<UserInfoPO> {
    @Value("表或视图名,可以配置成yml配置文件中的变量;" +
            "如果名称固定不变,只需要对应的po中PhoenixTable指定即可,此处不用设置")
    private String tableName;
    @PostConstruct
    private void init() {
        setTableName(this.tableName);
    }


    public UserInfoPO findByRowKey(String rowKey) {
        try {
            PhoenixLambdaQueryWrapper<UserInfoPO> lambdaQuery = BDWrappers.Phoenix.lambdaQuery(UserInfoPO.class);
            lambdaQuery.eq(BookStudyLogPhoenixViewPO::getLogRowKey, rowKey);
            lambdaQuery.orderByDesc(UserInfoPO::getUpdateDate);
            lambdaQuery.limit(null,1);
            List<UserInfoPO> poList = this.selectList(lambdaQuery);
            if(CollectionUtil.isNotEmpty(poList)){
                return poList.get(0);
            }

            return null;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```
#### 2.3.3 服务调用
```java
@Service
public class UserInfoViewService {

    @Autowired
    private UserInfoPhoenixMapper userInfoPhoenixMapper;

    @Override
    public UserInfoPO findOneByRowKey(String rowKey) {
        if(StrUtil.isBlank(rowKey)){
            return null;
        }

        return this.userInfoPhoenixMapper.findByRowKey(rowKey);
    }
    
}
```
