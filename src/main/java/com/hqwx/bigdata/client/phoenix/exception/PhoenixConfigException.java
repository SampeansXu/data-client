package com.hqwx.bigdata.client.phoenix.exception;

/**
 * @Description: PhoenixInitializeException
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
public class PhoenixConfigException extends PhoenixBaseException {
    public PhoenixConfigException() {
        super();
    }

    public PhoenixConfigException(String message) {
        super(message);
    }

    public PhoenixConfigException(Throwable cause) {
        super(cause);
    }

    public PhoenixConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
