package com.campustrade.common.rpc.registry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Service instance descriptor in registry.
 */
public class RpcServiceInstance implements Serializable {
    private String serviceName;
    private String host;
    private int port;
    private int weight = 100;
    private Map<String, String> metadata = new HashMap<>();
    private long registerTimestamp = System.currentTimeMillis();

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata == null ? new HashMap<>() : new HashMap<>(metadata);
    }

    public long getRegisterTimestamp() {
        return registerTimestamp;
    }

    public void setRegisterTimestamp(long registerTimestamp) {
        this.registerTimestamp = registerTimestamp;
    }

    public boolean sameEndpoint(RpcServiceInstance other) {
        if (other == null) {
            return false;
        }
        return Objects.equals(this.serviceName, other.serviceName)
                && Objects.equals(this.host, other.host)
                && this.port == other.port;
    }
}

