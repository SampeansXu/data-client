package com.hqwx.bigdata.client;

import com.hqwx.bigdata.client.common.entity.EntityProperty;
import com.hqwx.bigdata.client.common.sql.constant.SqlConstant;
import com.hqwx.bigdata.client.common.wrapper.BDWrappers;
import com.hqwx.bigdata.client.config.PhoenixManagerAutoConfig;
import com.hqwx.bigdata.client.mapper.UserInfoPhoenixMapper;
import com.hqwx.bigdata.client.phoenix.enums.PhoenixSQLKeyword;
import com.hqwx.bigdata.client.phoenix.parser.PhoenixEntityPropertiesParser;
import com.hqwx.bigdata.client.phoenix.parser.PhoenixSQLUtil;
import com.hqwx.bigdata.client.phoenix.wrapper.PhoenixLambdaQueryWrapper;
import com.hqwx.bigdata.client.po.UserInfoPO;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: Test_Phoenix
 * @Author: XuShengBin
 * @Date: 2022-08-30
 * @Ver: v1.0 -create
 */
@SpringBootTest(classes = {
        PhoenixManagerAutoConfig.class,
        UserInfoPhoenixMapper.class
})
public class Test_Phoenix {
    @Autowired
    UserInfoPhoenixMapper userInfoPhoenixMapper;

    private String getSaveSQL(UserInfoPO po) {
        final String tableName = "TEST_USER_STUDY_LOG";
        PhoenixEntityPropertiesParser<UserInfoPO> parser = new PhoenixEntityPropertiesParser<UserInfoPO>(po);
        Map<String, EntityProperty> field2PropertyMap = parser.getField2PropertyMap();
        String columnsList = "";
        String valuesList = "";
        for (EntityProperty property : field2PropertyMap.values()) {
            if (Objects.isNull(property.getValue())) {
                continue;
            }
            if (StringUtils.isNotBlank(columnsList)) {
                columnsList += SqlConstant.Separator_Comma;
            }
            columnsList += property.getFullColumnName();

            if (StringUtils.isNotBlank(valuesList)) {
                valuesList += SqlConstant.Separator_Comma;
            }
            valuesList += PhoenixSQLUtil.property2SQLStr(property.getValue());
        }

        String sqlSave = PhoenixSQLKeyword.UPSERT_INTO + " " + tableName +
                " (" + columnsList + ")" +
                " VALUES(" + valuesList + ")";
        return sqlSave;
    }

    private void savePO() {
        UserInfoPO po = new UserInfoPO();
        po.setBizUuid("TST00001-BD2F-4D43-B7E8-17AAC0D0B8DA");
        po.setUid(103453390L);
        po.setGoodsId(10992L);
        po.setObjId(446748L);
        po.setObjType((short) 10);
        po.setStartTime(new Date());
        po.setEndTime(new Date());
        po.setNoteCount(6);
        po.setStudyCount(50673);
        po.setResourceTotal(100000);
        po.setStudyDuration(60 * 60L);

        String saveSQL = this.getSaveSQL(po);
        System.out.println(saveSQL);
//        String sql = createSql(po);
//        System.out.println(sql);
    }

    private void select(){
        PhoenixLambdaQueryWrapper<UserInfoPO> lambdaWrapper = BDWrappers.Phoenix.lambdaQuery(UserInfoPO.class);
        lambdaWrapper.addSqlSelect(UserInfoPO::getLogRowKey, UserInfoPO::getBizUuid)
                .addSqlSelect(UserInfoPO::getUid);
        lambdaWrapper.addSqlSelect(UserInfoPO::getStartTime);
        lambdaWrapper.eq(UserInfoPO::getLogRowKey,"TestLogRowKey");
        lambdaWrapper.between(UserInfoPO::getGoodsName, "TestName1", "TestName2");
        lambdaWrapper.between(UserInfoPO::getStartTime, new Date(), new Date());
        lambdaWrapper.and(w -> w.lt(UserInfoPO::getBizUuid, 1000).or().le(UserInfoPO::getObjName, "TestName"));
        lambdaWrapper.orderBy(true, UserInfoPO::getGoodsName);
        lambdaWrapper.orderByDesc(UserInfoPO::getCreateDate);
        lambdaWrapper.groupBy(UserInfoPO::getObjType);
        lambdaWrapper.limit(0,10);
        List<UserInfoPO> poList = this.userInfoPhoenixMapper.selectList(lambdaWrapper);
        System.out.println(poList);


//        PhoenixQueryWrapper<UserInfoPO> wrapper = BDWrappers.Phoenix.query(UserInfoPO.class);
//        wrapper.addSqlSelect("TestSelect", "bizUuid")
//                        .addSqlSelect("TestBetween");
//        wrapper.addSqlSelect("uid");
//        wrapper.eq(false,"TestEQ","eqVal");
//        wrapper.between(false,"TestBetween","b1","e1");
//        wrapper.like(false,"TestLike","likeVal");
//        wrapper.orderByAsc("Test");
//        wrapper.orderBy(false,"TestDesc");
//        wrapper.limit(null,10);
//        List<UserInfoPO> poList = this.userInfoPhoenixMapper.selectList(wrapper);
//        System.out.println(poList);
    }
    @Test
    public void test() throws Exception {
        this.select();
    }
}
