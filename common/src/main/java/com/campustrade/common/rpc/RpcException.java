package com.campustrade.common.rpc;

/**
 * RPC invocation exception for client side usage.
 */
public class RpcException extends RuntimeException {
    private final Integer errorCode;

    public RpcException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
