package com.hqwx.bigdata.client.phoenix.exception;

/**
 * @Description: PhoenixOperationException
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
public class PhoenixOperationException extends PhoenixBaseException{
    public PhoenixOperationException() {
    }

    public PhoenixOperationException(String message) {
        super(message);
    }

    public PhoenixOperationException(Throwable cause) {
        super(cause);
    }

    public PhoenixOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
