package com.campustrade.registrycenter.service;

import com.campustrade.common.rpc.registry.RpcServiceInstance;
import com.campustrade.registrycenter.config.RegistryCenterProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RegistryStore {
    private final RegistryCenterProperties properties;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<RegistryEntry>> serviceMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> sequenceMap = new ConcurrentHashMap<>();

    public RegistryStore(RegistryCenterProperties properties) {
        this.properties = properties;
    }

    public void register(RpcServiceInstance instance) {
        validate(instance);
        long now = System.currentTimeMillis();
        RpcServiceInstance normalized = cloneInstance(instance);
        normalized.setRegisterTimestamp(now);

        CopyOnWriteArrayList<RegistryEntry> entries =
                serviceMap.computeIfAbsent(instance.getServiceName(), ignored -> new CopyOnWriteArrayList<>());
        entries.removeIf(entry -> entry.sameEndpoint(instance.getServiceName(), instance.getHost(), instance.getPort()));
        entries.add(new RegistryEntry(normalized, now));
    }

    public void heartbeat(String serviceName, String host, int port) {
        if (isInvalidEndpoint(serviceName, host, port)) {
            return;
        }
        long now = System.currentTimeMillis();
        CopyOnWriteArrayList<RegistryEntry> entries = serviceMap.get(serviceName);
        if (entries == null || entries.isEmpty()) {
            return;
        }
        for (RegistryEntry entry : entries) {
            if (entry.sameEndpoint(serviceName, host, port)) {
                entry.touch(now);
                return;
            }
        }
    }

    public void unregister(String serviceName, String host, int port) {
        if (isInvalidEndpoint(serviceName, host, port)) {
            return;
        }
        CopyOnWriteArrayList<RegistryEntry> entries = serviceMap.get(serviceName);
        if (entries == null || entries.isEmpty()) {
            return;
        }
        entries.removeIf(entry -> entry.sameEndpoint(serviceName, host, port));
    }

    public List<RpcServiceInstance> list(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) {
            return List.of();
        }
        cleanupStale(serviceName);
        CopyOnWriteArrayList<RegistryEntry> entries = serviceMap.get(serviceName);
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }
        List<RpcServiceInstance> instances = new ArrayList<>(entries.size());
        for (RegistryEntry entry : entries) {
            instances.add(cloneInstance(entry.instance));
        }
        return instances;
    }

    public RpcServiceInstance select(String serviceName) {
        List<RpcServiceInstance> instances = list(serviceName);
        if (instances.isEmpty()) {
            return null;
        }
        AtomicInteger sequence = sequenceMap.computeIfAbsent(serviceName, ignored -> new AtomicInteger(0));
        int index = Math.floorMod(sequence.getAndIncrement(), instances.size());
        return instances.get(index);
    }

    public Map<String, List<RpcServiceInstance>> snapshot() {
        cleanupAll();
        Map<String, List<RpcServiceInstance>> snapshot = new ConcurrentHashMap<>();
        for (Map.Entry<String, CopyOnWriteArrayList<RegistryEntry>> entry : serviceMap.entrySet()) {
            List<RpcServiceInstance> instances = new ArrayList<>();
            for (RegistryEntry record : entry.getValue()) {
                instances.add(cloneInstance(record.instance));
            }
            snapshot.put(entry.getKey(), instances);
        }
        return snapshot;
    }

    @Scheduled(fixedDelayString = "${campus.registry.cleanup-interval-ms:5000}")
    public void scheduledCleanup() {
        cleanupAll();
    }

    private void cleanupAll() {
        for (String serviceName : serviceMap.keySet()) {
            cleanupStale(serviceName);
        }
    }

    private void cleanupStale(String serviceName) {
        CopyOnWriteArrayList<RegistryEntry> entries = serviceMap.get(serviceName);
        if (entries == null || entries.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        long ttlMs = Math.max(properties.getInstanceTtlMs(), 3000L);
        entries.removeIf(entry -> (now - entry.lastHeartbeatTime) > ttlMs);
    }

    private RpcServiceInstance cloneInstance(RpcServiceInstance source) {
        RpcServiceInstance target = new RpcServiceInstance();
        target.setServiceName(source.getServiceName());
        target.setHost(source.getHost());
        target.setPort(source.getPort());
        target.setWeight(source.getWeight());
        target.setMetadata(source.getMetadata());
        target.setRegisterTimestamp(source.getRegisterTimestamp());
        return target;
    }

    private void validate(RpcServiceInstance instance) {
        if (instance == null) {
            throw new IllegalArgumentException("registry instance cannot be null");
        }
        if (isInvalidEndpoint(instance.getServiceName(), instance.getHost(), instance.getPort())) {
            throw new IllegalArgumentException("registry endpoint is invalid");
        }
    }

    private boolean isInvalidEndpoint(String serviceName, String host, int port) {
        return serviceName == null || serviceName.isBlank()
                || host == null || host.isBlank()
                || port <= 0;
    }

    private static class RegistryEntry {
        private final RpcServiceInstance instance;
        private volatile long lastHeartbeatTime;

        private RegistryEntry(RpcServiceInstance instance, long lastHeartbeatTime) {
            this.instance = instance;
            this.lastHeartbeatTime = lastHeartbeatTime;
        }

        private boolean sameEndpoint(String serviceName, String host, int port) {
            return Objects.equals(instance.getServiceName(), serviceName)
                    && Objects.equals(instance.getHost(), host)
                    && instance.getPort() == port;
        }

        private void touch(long now) {
            this.lastHeartbeatTime = now;
        }
    }
}

