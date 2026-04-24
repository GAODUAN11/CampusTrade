package com.campustrade.common.rpc.protocol;

import java.io.IOException;

/**
 * Pluggable serializer SPI for RPC payload.
 */
public interface RpcSerializer {
    byte code();

    String name();

    byte[] serialize(Object value) throws IOException;

    <T> T deserialize(byte[] payload, Class<T> targetType) throws IOException, ClassNotFoundException;
}

