package com.hqwx.bigdata.client.hbase.constant;

/**
 * @Description: HBaseOperationsException
 * @Author: XuShengBin
 * @Date: 2022-08-27
 * @Ver: v1.0 -create
 */
public class HBaseOperationsException extends RuntimeException{
    public HBaseOperationsException() {
        super();
    }

    public HBaseOperationsException(String message) {
        super(message);
    }

    public HBaseOperationsException(Throwable cause) {
        super(cause);
    }

    public HBaseOperationsException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
