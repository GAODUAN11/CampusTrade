package com.campustrade.registrycenter.rpc;

import com.campustrade.common.rpc.RpcServiceHandler;
import com.campustrade.common.rpc.registry.RpcServiceInstance;
import com.campustrade.registrycenter.service.RegistryStore;
import org.springframework.stereotype.Component;

@Component
public class RegistryRpcServiceHandler implements RpcServiceHandler {
    private final RegistryStore registryStore;

    public RegistryRpcServiceHandler(RegistryStore registryStore) {
        this.registryStore = registryStore;
    }

    @Override
    public Object handle(String methodName, Object[] arguments) {
        return switch (methodName) {
            case "register" -> {
                registryStore.register((RpcServiceInstance) arguments[0]);
                yield null;
            }
            case "heartbeat" -> {
                registryStore.heartbeat(
                        (String) arguments[0],
                        (String) arguments[1],
                        toInt(arguments[2])
                );
                yield null;
            }
            case "unregister" -> {
                registryStore.unregister(
                        (String) arguments[0],
                        (String) arguments[1],
                        toInt(arguments[2])
                );
                yield null;
            }
            case "list" -> registryStore.list((String) arguments[0]);
            case "select" -> registryStore.select((String) arguments[0]);
            case "snapshot" -> registryStore.snapshot();
            default -> throw new IllegalArgumentException("Unsupported registry rpc method: " + methodName);
        };
    }

    private int toInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}

