package com.hqwx.bigdata.client.phoenix.constant;

/**
 * @Description: PhoenixConstant
 * @Author: XuShengBin
 * @Date: 2022-09-08
 * @Ver: v1.0 -create
 */
public class PhoenixConstant {
    //对应HBase列族与列名连接符
    public static final String Separator_Property = ".";
    //phoenix dataSource的默认名称
    public static final String DataSource_Name_Default = "default";

    public interface PhoenixTable_Type {
        String Table = "table";
        String View = "view";
    }
}
