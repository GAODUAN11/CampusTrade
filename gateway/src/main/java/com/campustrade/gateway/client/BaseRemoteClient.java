package com.campustrade.gateway.client;

import com.campustrade.common.rpc.RpcClient;
import com.campustrade.common.rpc.RpcException;
import com.campustrade.common.rpc.protocol.JavaRpcSerializer;
import com.campustrade.common.rpc.protocol.RpcProtocol;
import com.campustrade.common.rpc.registry.RpcServiceInstance;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.exception.RemoteServiceException;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;

import java.time.Duration;
import java.util.Map;

/**
 * Shared helper for remote RPC invocation.
 */
public abstract class BaseRemoteClient {
    private final GatewayRpcServiceLocator serviceLocator;

    protected BaseRemoteClient(GatewayRpcServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    protected <T> T callRpc(String serviceName,
                            GatewayRemoteProperties.ServiceEndpoint endpoint,
                            String methodName,
                            Object... args) {
        validateEndpoint(serviceName, endpoint);
        RpcServiceInstance instance = serviceLocator.resolve(serviceName, endpoint);
        byte protocolVersion = resolveProtocolVersion(instance, endpoint);
        String serializer = resolveSerializer(instance, endpoint);

        try {
            RpcClient client = new RpcClient(
                    instance.getHost(),
                    instance.getPort(),
                    Duration.ofMillis(endpoint.getTimeoutMs() == null ? 5000 : endpoint.getTimeoutMs()),
                    protocolVersion,
                    serializer
            );
            return client.invoke(serviceName, methodName, args);
        } catch (RpcException ex) {
            throw new RemoteServiceException(
                    "RPC " + serviceName + "." + methodName + " failed: " + ex.getMessage(), ex
            );
        } catch (Exception ex) {
            throw new RemoteServiceException(
                    "RPC " + serviceName + "." + methodName + " failed unexpectedly.", ex
            );
        }
    }

    private void validateEndpoint(String serviceName, GatewayRemoteProperties.ServiceEndpoint endpoint) {
        if (endpoint == null) {
            throw new RemoteServiceException("RPC endpoint config missing for " + serviceName);
        }
        if (endpoint.getHost() == null || endpoint.getHost().isBlank()) {
            throw new RemoteServiceException("RPC host missing for " + serviceName);
        }
        if (endpoint.getRpcPort() == null || endpoint.getRpcPort() <= 0) {
            throw new RemoteServiceException("RPC port missing for " + serviceName);
        }
    }

    private byte resolveProtocolVersion(RpcServiceInstance instance, GatewayRemoteProperties.ServiceEndpoint endpoint) {
        Byte endpointVersion = endpoint.getRpcVersion();
        byte defaultVersion = endpointVersion == null ? RpcProtocol.CURRENT_VERSION : endpointVersion;
        Map<String, String> metadata = instance.getMetadata();
        if (metadata == null) {
            return defaultVersion;
        }
        String value = metadata.get("version");
        if (value == null || value.isBlank()) {
            return defaultVersion;
        }
        try {
            byte parsed = Byte.parseByte(value.trim());
            return parsed > 0 ? parsed : defaultVersion;
        } catch (NumberFormatException ignored) {
            return defaultVersion;
        }
    }

    private String resolveSerializer(RpcServiceInstance instance, GatewayRemoteProperties.ServiceEndpoint endpoint) {
        String defaultSerializer = endpoint.getRpcSerializer() == null || endpoint.getRpcSerializer().isBlank()
                ? JavaRpcSerializer.NAME
                : endpoint.getRpcSerializer().trim();
        Map<String, String> metadata = instance.getMetadata();
        if (metadata == null) {
            return defaultSerializer;
        }
        String serializer = metadata.get("serializer");
        if (serializer == null || serializer.isBlank()) {
            return defaultSerializer;
        }
        return serializer.trim();
    }
}
