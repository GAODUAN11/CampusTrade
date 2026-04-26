package com.campustrade.userservice.rpc;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.rpc.RpcServer;
import com.campustrade.common.rpc.RpcServiceHandler;
import com.campustrade.common.rpc.protocol.JavaRpcSerializer;
import com.campustrade.common.rpc.registry.RegistryCenterClient;
import com.campustrade.common.rpc.registry.RpcServiceInstance;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class UserRpcServerBootstrap implements ApplicationRunner, DisposableBean {
    private final RpcServer rpcServer;
    private final int rpcPort;
    private final byte protocolVersion;
    private final String serializer;
    private final boolean registryEnabled;
    private final String advertiseHost;
    private final String registryHost;
    private final int registryPort;
    private final int registryTimeoutMs;
    private final int heartbeatIntervalMs;
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile RegistryCenterClient registryCenterClient;

    public UserRpcServerBootstrap(@Value("${campus.rpc.port:9091}") int rpcPort,
                                  @Value("${campus.rpc.protocol-version:1}") byte protocolVersion,
                                  @Value("${campus.rpc.serializer:java}") String serializer,
                                  @Value("${campus.rpc.advertise-host:127.0.0.1}") String advertiseHost,
                                  @Value("${campus.rpc.registry.enabled:false}") boolean registryEnabled,
                                  @Value("${campus.rpc.registry.host:127.0.0.1}") String registryHost,
                                  @Value("${campus.rpc.registry.port:9090}") int registryPort,
                                  @Value("${campus.rpc.registry.timeout-ms:3000}") int registryTimeoutMs,
                                  @Value("${campus.rpc.registry.heartbeat-interval-ms:5000}") int heartbeatIntervalMs,
                                  UserRpcServiceHandler userRpcServiceHandler) {
        this.rpcPort = rpcPort;
        this.protocolVersion = protocolVersion;
        this.serializer = serializer;
        this.advertiseHost = advertiseHost;
        this.registryEnabled = registryEnabled;
        this.registryHost = registryHost;
        this.registryPort = registryPort;
        this.registryTimeoutMs = registryTimeoutMs;
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.rpcServer = new RpcServer(
                rpcPort,
                Map.of(ServiceNames.USER_SERVICE, (RpcServiceHandler) userRpcServiceHandler),
                Runtime.getRuntime().availableProcessors(),
                protocolVersion
        );
    }

    @Override
    public void run(ApplicationArguments args) {
        rpcServer.start();
        registerToRegistryIfEnabled();
    }

    @Override
    public void destroy() {
        unregisterFromRegistryIfEnabled();
        heartbeatScheduler.shutdownNow();
        rpcServer.stop();
    }

    private void registerToRegistryIfEnabled() {
        if (!registryEnabled) {
            return;
        }
        try {
            registryClient().register(buildInstance());
            heartbeatScheduler.scheduleAtFixedRate(
                    this::safeHeartbeat,
                    Math.max(heartbeatIntervalMs, 3000),
                    Math.max(heartbeatIntervalMs, 3000),
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception ignored) {
            // Avoid blocking service startup if registry-center is temporarily unavailable.
        }
    }

    private void safeHeartbeat() {
        try {
            registryClient().heartbeat(ServiceNames.USER_SERVICE, advertiseHost, rpcPort);
        } catch (Exception ignored) {
        }
    }

    private void unregisterFromRegistryIfEnabled() {
        if (!registryEnabled || registryCenterClient == null) {
            return;
        }
        try {
            registryCenterClient.unregister(ServiceNames.USER_SERVICE, advertiseHost, rpcPort);
        } catch (Exception ignored) {
        }
    }

    private RegistryCenterClient registryClient() {
        RegistryCenterClient client = registryCenterClient;
        if (client != null) {
            return client;
        }
        synchronized (this) {
            if (registryCenterClient == null) {
                registryCenterClient = new RegistryCenterClient(
                        registryHost,
                        registryPort,
                        Duration.ofMillis(Math.max(registryTimeoutMs, 1000)),
                        protocolVersion,
                        normalizeSerializer(serializer)
                );
            }
            return registryCenterClient;
        }
    }

    private RpcServiceInstance buildInstance() {
        RpcServiceInstance instance = new RpcServiceInstance();
        instance.setServiceName(ServiceNames.USER_SERVICE);
        instance.setHost(advertiseHost);
        instance.setPort(rpcPort);
        instance.setMetadata(buildMetadata());
        return instance;
    }

    private Map<String, String> buildMetadata() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("version", String.valueOf(protocolVersion));
        metadata.put("serializer", normalizeSerializer(serializer));
        return metadata;
    }

    private String normalizeSerializer(String value) {
        if (value == null || value.isBlank()) {
            return JavaRpcSerializer.NAME;
        }
        return value.trim();
    }
}
