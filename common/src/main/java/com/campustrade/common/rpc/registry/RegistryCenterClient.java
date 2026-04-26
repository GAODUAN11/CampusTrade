package com.campustrade.common.rpc.registry;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.rpc.RpcClient;
import com.campustrade.common.rpc.protocol.JavaRpcSerializer;
import com.campustrade.common.rpc.protocol.RpcProtocol;

import java.time.Duration;
import java.util.List;

/**
 * Lightweight client for the standalone registry-center service.
 */
public class RegistryCenterClient {
    private final RpcClient rpcClient;

    public RegistryCenterClient(String host, int port, Duration timeout) {
        this(host, port, timeout, RpcProtocol.CURRENT_VERSION, JavaRpcSerializer.NAME);
    }

    public RegistryCenterClient(String host,
                                int port,
                                Duration timeout,
                                byte protocolVersion,
                                String serializer) {
        this.rpcClient = new RpcClient(host, port, timeout, protocolVersion, serializer);
    }

    public void register(RpcServiceInstance instance) {
        rpcClient.invoke(ServiceNames.REGISTRY_CENTER, "register", instance);
    }

    public void heartbeat(String serviceName, String host, int port) {
        rpcClient.invoke(ServiceNames.REGISTRY_CENTER, "heartbeat", serviceName, host, port);
    }

    public void unregister(String serviceName, String host, int port) {
        rpcClient.invoke(ServiceNames.REGISTRY_CENTER, "unregister", serviceName, host, port);
    }

    @SuppressWarnings("unchecked")
    public List<RpcServiceInstance> list(String serviceName) {
        return rpcClient.invoke(ServiceNames.REGISTRY_CENTER, "list", serviceName);
    }
}

