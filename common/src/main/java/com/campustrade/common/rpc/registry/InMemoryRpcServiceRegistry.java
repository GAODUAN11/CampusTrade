package com.campustrade.common.rpc.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory registry skeleton with round-robin selection.
 */
public class InMemoryRpcServiceRegistry implements RpcServiceRegistry {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<RpcServiceInstance>> serviceMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> sequenceMap = new ConcurrentHashMap<>();

    @Override
    public void register(RpcServiceInstance instance) {
        validate(instance);
        CopyOnWriteArrayList<RpcServiceInstance> instances =
                serviceMap.computeIfAbsent(instance.getServiceName(), ignored -> new CopyOnWriteArrayList<>());
        instances.removeIf(existing -> existing.sameEndpoint(instance));
        instances.add(instance);
    }

    @Override
    public void unregister(String serviceName, String host, int port) {
        if (serviceName == null || serviceName.isBlank() || host == null || host.isBlank() || port <= 0) {
            return;
        }
        CopyOnWriteArrayList<RpcServiceInstance> instances = serviceMap.get(serviceName);
        if (instances == null || instances.isEmpty()) {
            return;
        }
        instances.removeIf(existing -> host.equals(existing.getHost()) && port == existing.getPort());
    }

    @Override
    public List<RpcServiceInstance> list(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) {
            return List.of();
        }
        CopyOnWriteArrayList<RpcServiceInstance> instances = serviceMap.get(serviceName);
        if (instances == null || instances.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(instances);
    }

    @Override
    public Optional<RpcServiceInstance> select(String serviceName) {
        CopyOnWriteArrayList<RpcServiceInstance> instances = serviceMap.get(serviceName);
        if (instances == null || instances.isEmpty()) {
            return Optional.empty();
        }
        AtomicInteger sequence = sequenceMap.computeIfAbsent(serviceName, ignored -> new AtomicInteger(0));
        int index = Math.floorMod(sequence.getAndIncrement(), instances.size());
        return Optional.of(instances.get(index));
    }

    private void validate(RpcServiceInstance instance) {
        if (instance == null) {
            throw new IllegalArgumentException("instance cannot be null");
        }
        if (instance.getServiceName() == null || instance.getServiceName().isBlank()) {
            throw new IllegalArgumentException("serviceName cannot be blank");
        }
        if (instance.getHost() == null || instance.getHost().isBlank()) {
            throw new IllegalArgumentException("host cannot be blank");
        }
        if (instance.getPort() <= 0) {
            throw new IllegalArgumentException("port must be > 0");
        }
    }
}

