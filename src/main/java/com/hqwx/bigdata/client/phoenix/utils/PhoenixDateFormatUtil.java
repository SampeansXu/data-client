package com.hqwx.bigdata.client.phoenix.utils;

import com.hqwx.bigdata.client.phoenix.exception.PhoenixOperationException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @Description: PhoenixDateFormatUtil
 * @Author: XuShengBin
 * @Date: 2022-09-05
 * @Ver: v1.0 -create
 */
public class PhoenixDateFormatUtil {

    public static final String formatDate(Date date) {
        if(Objects.isNull(date)){
            throw new PhoenixOperationException("date is null");
        }

        final String TimeStamp_Format = "yyyy-MM-dd HH:mm:ss";
        String formattedDate = new SimpleDateFormat(TimeStamp_Format).format(date);
        StringBuffer dateFormatBuilder = new StringBuffer("TO_DATE('");
        dateFormatBuilder.append(formattedDate).append("', '"+TimeStamp_Format+"')");
        return dateFormatBuilder.toString();
    }

    public static final String formatDateZone(Date date) {
        if(Objects.isNull(date)){
            throw new PhoenixOperationException("date is null");
        }

        final String TimeStamp_Format = "yyyy-MM-dd HH:mm:ss z";
        String formattedDate = new SimpleDateFormat(TimeStamp_Format).format(date);
        StringBuffer dateFormatBuilder = new StringBuffer("TO_DATE('");
        dateFormatBuilder.append(formattedDate).append("', '"+TimeStamp_Format+"')");
        return dateFormatBuilder.toString();
    }
}
