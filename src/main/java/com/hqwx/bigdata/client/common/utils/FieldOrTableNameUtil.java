package com.hqwx.bigdata.client.common.utils;

/**
 * @Description: 驼峰命名法与下划线命名的相互转换
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
public class FieldOrTableNameUtil {
    /**
     * 驼峰命名法转换为下划线
     *
     * @param camelCaseName 驼峰命名法的字段名
     * @return 下划线字段名
     */
    public static String underscoreName(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (camelCaseName != null && camelCaseName.length() > 0) {
            result.append(camelCaseName.substring(0, 1).toLowerCase());
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    /**
     * 下划线命名转换为驼峰
     *
     * @param underscoreName 下划线字段名
     * @return 驼峰命名法
     */
    public static String camelCaseName(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ("_".charAt(0) == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(underscoreName("UserPojo"));
        System.out.println(underscoreName("isExists"));
        System.out.println(underscoreName("isUsefulName"));
        System.out.println(camelCaseName("is_useful_name"));
    }
}
