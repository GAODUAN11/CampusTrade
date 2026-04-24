package com.campustrade.common.rpc.registry;

import java.util.List;
import java.util.Optional;

/**
 * Minimal registry abstraction.
 */
public interface RpcServiceRegistry {
    void register(RpcServiceInstance instance);

    void unregister(String serviceName, String host, int port);

    List<RpcServiceInstance> list(String serviceName);

    Optional<RpcServiceInstance> select(String serviceName);
}

