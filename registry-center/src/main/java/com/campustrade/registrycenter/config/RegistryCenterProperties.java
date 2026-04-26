package com.campustrade.registrycenter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "campus.registry")
public class RegistryCenterProperties {
    private int rpcPort = 9090;
    private byte protocolVersion = 1;
    private String serializer = "java";
    private long instanceTtlMs = 15000L;
    private long cleanupIntervalMs = 5000L;

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public long getInstanceTtlMs() {
        return instanceTtlMs;
    }

    public void setInstanceTtlMs(long instanceTtlMs) {
        this.instanceTtlMs = instanceTtlMs;
    }

    public long getCleanupIntervalMs() {
        return cleanupIntervalMs;
    }

    public void setCleanupIntervalMs(long cleanupIntervalMs) {
        this.cleanupIntervalMs = cleanupIntervalMs;
    }
}

