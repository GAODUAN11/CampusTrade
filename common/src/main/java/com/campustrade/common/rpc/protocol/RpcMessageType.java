package com.campustrade.common.rpc.protocol;

/**
 * RPC transport message type.
 */
public enum RpcMessageType {
    REQUEST((byte) 1),
    RESPONSE((byte) 2);

    private final byte code;

    RpcMessageType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static RpcMessageType fromCode(byte code) {
        for (RpcMessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported RPC message type code: " + code);
    }
}

