package com.campustrade.gateway.rpc;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.rpc.protocol.JavaRpcSerializer;
import com.campustrade.common.rpc.protocol.RpcProtocol;
import com.campustrade.common.rpc.registry.InMemoryRpcServiceRegistry;
import com.campustrade.common.rpc.registry.RpcServiceInstance;
import com.campustrade.common.rpc.registry.RpcServiceRegistry;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Gateway-side service locator.
 *
 * <p>Current phase uses an in-memory registry initialized by static configuration.
 * Later this bean can be replaced by remote registry discovery without changing callers.</p>
 */
@Component
public class GatewayRpcServiceLocator {
    private final RpcServiceRegistry registry = new InMemoryRpcServiceRegistry();
    private final GatewayRemoteProperties properties;

    public GatewayRpcServiceLocator(GatewayRemoteProperties properties) {
        this.properties = properties;
        bootstrapFromConfig();
    }

    public RpcServiceInstance resolve(String serviceName, GatewayRemoteProperties.ServiceEndpoint fallbackEndpoint) {
        return registry.select(serviceName).orElseGet(() -> toInstance(serviceName, fallbackEndpoint));
    }

    private void bootstrapFromConfig() {
        registerIfPresent(ServiceNames.USER_SERVICE, properties.getUserService());
        registerIfPresent(ServiceNames.PRODUCT_SERVICE, properties.getProductService());
        registerIfPresent(ServiceNames.FAVORITE_SERVICE, properties.getFavoriteService());
        registerIfPresent(ServiceNames.MESSAGE_SERVICE, properties.getMessageService());
    }

    private void registerIfPresent(String serviceName, GatewayRemoteProperties.ServiceEndpoint endpoint) {
        if (endpoint == null || endpoint.getHost() == null || endpoint.getHost().isBlank()) {
            return;
        }
        if (endpoint.getRpcPort() == null || endpoint.getRpcPort() <= 0) {
            return;
        }
        registry.register(toInstance(serviceName, endpoint));
    }

    private RpcServiceInstance toInstance(String serviceName, GatewayRemoteProperties.ServiceEndpoint endpoint) {
        RpcServiceInstance instance = new RpcServiceInstance();
        instance.setServiceName(serviceName);
        instance.setHost(endpoint.getHost());
        instance.setPort(endpoint.getRpcPort() == null ? 0 : endpoint.getRpcPort());
        instance.setMetadata(buildMetadata(endpoint));
        return instance;
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
}

