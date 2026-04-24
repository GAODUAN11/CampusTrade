package com.campustrade.common.rpc.protocol;

import java.io.IOException;

/**
 * RPC protocol-level exception.
 */
public class RpcProtocolException extends IOException {
    public RpcProtocolException(String message) {
        super(message);
    }

    public RpcProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}

