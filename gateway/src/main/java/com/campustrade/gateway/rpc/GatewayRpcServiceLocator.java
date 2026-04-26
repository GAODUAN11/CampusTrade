package com.campustrade.gateway.rpc;

import com.campustrade.common.rpc.registry.RegistryCenterClient;
import com.campustrade.common.rpc.protocol.JavaRpcSerializer;
import com.campustrade.common.rpc.protocol.RpcProtocol;
import com.campustrade.common.rpc.registry.RpcServiceInstance;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gateway-side service locator.
 *
 * <p>In RPC mode, it discovers providers from registry-center first.
 * If registry is unavailable, it falls back to static endpoint config.</p>
 */
@Component
public class GatewayRpcServiceLocator {
    private final GatewayRemoteProperties properties;
    private final ConcurrentHashMap<String, AtomicInteger> sequenceMap = new ConcurrentHashMap<>();
    private volatile RegistryCenterClient registryCenterClient;

    public GatewayRpcServiceLocator(GatewayRemoteProperties properties) {
        this.properties = properties;
    }

    public List<RpcServiceInstance> resolveCandidates(String serviceName,
                                                      GatewayRemoteProperties.ServiceEndpoint fallbackEndpoint) {
        List<RpcServiceInstance> discovered = discoverFromRegistry(serviceName);
        if (!discovered.isEmpty()) {
            return reorderByRoundRobin(serviceName, discovered);
        }
        RpcServiceInstance fallback = toInstance(serviceName, fallbackEndpoint);
        if (fallback == null) {
            return List.of();
        }
        return List.of(fallback);
    }

    private Map<String, String> buildMetadata(GatewayRemoteProperties.ServiceEndpoint endpoint) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("serializer", normalizeSerializer(endpoint.getRpcSerializer()));
        metadata.put("version", String.valueOf(normalizeVersion(endpoint.getRpcVersion())));
        return metadata;
    }

    private String normalizeSerializer(String serializer) {
        if (serializer == null || serializer.isBlank()) {
            return JavaRpcSerializer.NAME;
        }
        return serializer.trim();
    }

    private byte normalizeVersion(Byte version) {
        if (version == null || version <= 0) {
            return RpcProtocol.CURRENT_VERSION;
        }
        return version;
    }

    private List<RpcServiceInstance> discoverFromRegistry(String serviceName) {
        if (!properties.isRpcMode()) {
            return List.of();
        }
        GatewayRemoteProperties.RegistryEndpoint registry = properties.getRegistry();
        if (registry == null || !registry.isEnabled()) {
            return List.of();
        }

        try {
            List<RpcServiceInstance> instances = registryClient(registry).list(serviceName);
            if (instances == null || instances.isEmpty()) {
                return List.of();
            }
            List<RpcServiceInstance> active = new ArrayList<>();
            for (RpcServiceInstance instance : instances) {
                if (instance == null) {
                    continue;
                }
                if (instance.getHost() == null || instance.getHost().isBlank() || instance.getPort() <= 0) {
                    continue;
                }
                active.add(instance);
            }
            return active;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private List<RpcServiceInstance> reorderByRoundRobin(String serviceName, List<RpcServiceInstance> instances) {
        if (instances.size() <= 1) {
            return instances;
        }
        AtomicInteger sequence = sequenceMap.computeIfAbsent(serviceName, ignored -> new AtomicInteger(0));
        int offset = Math.floorMod(sequence.getAndIncrement(), instances.size());
        if (offset == 0) {
            return instances;
        }
        List<RpcServiceInstance> reordered = new ArrayList<>(instances.size());
        for (int i = 0; i < instances.size(); i++) {
            reordered.add(instances.get((offset + i) % instances.size()));
        }
        return reordered;
    }

    private RpcServiceInstance toInstance(String serviceName, GatewayRemoteProperties.ServiceEndpoint endpoint) {
        if (endpoint == null) {
            return null;
        }
        if (endpoint.getHost() == null || endpoint.getHost().isBlank()) {
            return null;
        }
        if (endpoint.getRpcPort() == null || endpoint.getRpcPort() <= 0) {
            return null;
        }
        RpcServiceInstance instance = new RpcServiceInstance();
        instance.setServiceName(serviceName);
        instance.setHost(endpoint.getHost());
        instance.setPort(endpoint.getRpcPort());
        instance.setMetadata(buildMetadata(endpoint));
        return instance;
    }

    private RegistryCenterClient registryClient(GatewayRemoteProperties.RegistryEndpoint endpoint) {
        RegistryCenterClient client = registryCenterClient;
        if (client != null) {
            return client;
        }
        synchronized (this) {
            if (registryCenterClient == null) {
                registryCenterClient = new RegistryCenterClient(
                        endpoint.getHost(),
                        endpoint.getRpcPort() == null ? 9090 : endpoint.getRpcPort(),
                        Duration.ofMillis(endpoint.getTimeoutMs() == null ? 3000 : endpoint.getTimeoutMs()),
                        normalizeVersion(endpoint.getRpcVersion()),
                        normalizeSerializer(endpoint.getRpcSerializer())
                );
            }
            return registryCenterClient;
        }
    }
}
