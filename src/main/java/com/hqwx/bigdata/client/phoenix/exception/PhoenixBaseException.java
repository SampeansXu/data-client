package com.hqwx.bigdata.client.phoenix.exception;

/**
 * @Description: PhoenixBaseException
 * @Author: XuShengBin
 * @Date: 2022-08-28
 * @Ver: v1.0 -create
 */
public class PhoenixBaseException extends RuntimeException{
    public PhoenixBaseException(){

    }
    public PhoenixBaseException(String message) {
        super(message);
    }

    public PhoenixBaseException(Throwable cause) {
        super(cause);
    }

    public PhoenixBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
