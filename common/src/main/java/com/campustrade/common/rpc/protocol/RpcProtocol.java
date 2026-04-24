package com.campustrade.common.rpc.protocol;

/**
 * RPC protocol constants.
 */
public final class RpcProtocol {
    public static final int MAGIC = 0x43545250; // CTRP
    public static final byte VERSION_V1 = 1;
    public static final byte CURRENT_VERSION = VERSION_V1;
    public static final int MAX_BODY_LENGTH = 10 * 1024 * 1024;

    private RpcProtocol() {
    }

    public static boolean isSupportedVersion(byte version) {
        return version == VERSION_V1;
    }
}

