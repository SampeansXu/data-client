package com.hqwx.bigdata.client.phoenix.exception;

/**
 * @Description: PhoenixOperationException
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
public class PhoenixManagerException extends PhoenixBaseException{
    public PhoenixManagerException() {
    }

    public PhoenixManagerException(String message) {
        super(message);
    }

    public PhoenixManagerException(Throwable cause) {
        super(cause);
    }

    public PhoenixManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
