package com.hqwx.bigdata.client.po;

import com.hqwx.bigdata.client.phoenix.annotation.PhoenixColumn;
import com.hqwx.bigdata.client.phoenix.annotation.PhoenixTable;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: UserInfoPO
 * @Author: XuShengBin
 * @Date: 2022-08-30
 * @Ver: v1.0 -create
 */
@Data
@PhoenixTable(type = PhoenixTable.Type.View, name = "TEST_BOOK_STUDY_LOG_VIEW")
public class UserInfoPO implements Serializable {
    @PhoenixColumn(name = "LOG_ROWKEY")
    private String logRowKey;

    public String getLogRowKey() {
        if(StringUtils.isNotBlank(logRowKey)){
            return this.logRowKey;
        }

        final Integer RegionSplitsUid_Mod = 10;
        //Region_uid_bizUuid
        final String Separator = "_";
        this.logRowKey = (this.uid % RegionSplitsUid_Mod) + Separator +
                this.uid + Separator + this.bizUuid.replaceAll("-", "");
        return this.logRowKey;
    }

    @PhoenixColumn(name = "BIZ_UUID", family = "BASE_INFO")
    private String bizUuid; //业务方uuid

    @PhoenixColumn(name = "UID", family = "BASE_INFO")
    private Long uid;//用户uid

    @PhoenixColumn(name = "GOODS_ID", family = "BASE_INFO")
    private Long goodsId; //商品id

    @PhoenixColumn(name = "GOODS_NAME", family = "BASE_INFO")
    private Long goodsName; //商品名称

    @PhoenixColumn(name = "OBJ_ID", family = "BASE_INFO")
    private Long objId; //"产品资源id"

    @PhoenixColumn(name = "OBJ_TYPE", family = "BASE_INFO")
    private Short objType; //"产品资源类型:音视频、直播、电子书"

    @PhoenixColumn(name = "OBJ_NAME", family = "BASE_INFO")
    private Long objName; //"产品资源名称"

    @PhoenixColumn(name = "SECOND_CATEGORY_ID", family = "BASE_INFO")
    private Long secondCategoryId; //"所属考试id"

    @PhoenixColumn(name = "CATEGORY_ID", family = "BASE_INFO")
    private Long categoryId; //"所属科目id"

    @PhoenixColumn(name = "APPID", family = "BASE_INFO")
    private String appId; //"来源应用 web 环球App 小程序"

    @PhoenixColumn(name = "PLATEFORM", family = "BASE_INFO")
    private String plateForm; //"来源终端 android iOS web h5"

    @PhoenixColumn(name = "STUDY_DURATION", family = "BASE_INFO")
    private Long studyDuration; //"学习时长 单位:秒"

    @PhoenixColumn(name = "START_TIME", family = "BASE_INFO")
    private Date startTime; //"学习开始时间"

    @PhoenixColumn(name = "END_TIME", family = "BASE_INFO")
    private Date endTime; //"学习结束时间"

    @PhoenixColumn(name = "CREATE_DATE", family = "BASE_INFO")
    private Date createDate; //"创建时间"

    @PhoenixColumn(name = "UPDATE_DATE", family = "BASE_INFO")
    private Date updateDate; //"更新时间"

    @PhoenixColumn(name = "NOTE_COUNT", family = "RESOURCE_INFO")
    private Integer noteCount; //"笔记数量"

    @PhoenixColumn(name = "STUDY_COUNT", family = "RESOURCE_INFO")
    private Integer studyCount; //"学习量,如书的阅读字数"

    @PhoenixColumn(name = "RESOURCE_TOTAL", family = "RESOURCE_INFO")
    private Integer resourceTotal; //"资源总量,如书的总字数"
}
