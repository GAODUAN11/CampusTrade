package com.campustrade.common.rpc.protocol;

/**
 * Decoded packet with protocol metadata.
 */
public class RpcTransportPacket<T> {
    private final byte protocolVersion;
    private final RpcSerializer serializer;
    private final T payload;

    public RpcTransportPacket(byte protocolVersion, RpcSerializer serializer, T payload) {
        this.protocolVersion = protocolVersion;
        this.serializer = serializer;
        this.payload = payload;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public T getPayload() {
        return payload;
    }
}

