package com.campustrade.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "campus.remote")
public class GatewayRemoteProperties {
    private String mode = "http";
    private RegistryEndpoint registry = new RegistryEndpoint();
    private ServiceEndpoint userService = new ServiceEndpoint();
    private ServiceEndpoint productService = new ServiceEndpoint();
    private ServiceEndpoint favoriteService = new ServiceEndpoint();
    private ServiceEndpoint messageService = new ServiceEndpoint();

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public RegistryEndpoint getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryEndpoint registry) {
        this.registry = registry;
    }

    public ServiceEndpoint getUserService() {
        return userService;
    }

    public void setUserService(ServiceEndpoint userService) {
        this.userService = userService;
    }

    public ServiceEndpoint getProductService() {
        return productService;
    }

    public void setProductService(ServiceEndpoint productService) {
        this.productService = productService;
    }

    public ServiceEndpoint getFavoriteService() {
        return favoriteService;
    }

    public void setFavoriteService(ServiceEndpoint favoriteService) {
        this.favoriteService = favoriteService;
    }

    public ServiceEndpoint getMessageService() {
        return messageService;
    }

    public void setMessageService(ServiceEndpoint messageService) {
        this.messageService = messageService;
    }

    public static class ServiceEndpoint {
        private String host = "127.0.0.1";
        private Integer rpcPort;
        private Integer timeoutMs = 5000;
        private Byte rpcVersion = 1;
        private String rpcSerializer = "java";
        private String baseUrl;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getRpcPort() {
            return rpcPort;
        }

        public void setRpcPort(Integer rpcPort) {
            this.rpcPort = rpcPort;
        }

        public Integer getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(Integer timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public Byte getRpcVersion() {
            return rpcVersion;
        }

        public void setRpcVersion(Byte rpcVersion) {
            this.rpcVersion = rpcVersion;
        }

        public String getRpcSerializer() {
            return rpcSerializer;
        }

        public void setRpcSerializer(String rpcSerializer) {
            this.rpcSerializer = rpcSerializer;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class RegistryEndpoint {
        private boolean enabled = true;
        private String host = "127.0.0.1";
        private Integer rpcPort = 9090;
        private Integer timeoutMs = 3000;
        private Byte rpcVersion = 1;
        private String rpcSerializer = "java";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getRpcPort() {
            return rpcPort;
        }

        public void setRpcPort(Integer rpcPort) {
            this.rpcPort = rpcPort;
        }

        public Integer getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(Integer timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public Byte getRpcVersion() {
            return rpcVersion;
        }

        public void setRpcVersion(Byte rpcVersion) {
            this.rpcVersion = rpcVersion;
        }

        public String getRpcSerializer() {
            return rpcSerializer;
        }

        public void setRpcSerializer(String rpcSerializer) {
            this.rpcSerializer = rpcSerializer;
        }
    }

    public boolean isRpcMode() {
        return "rpc".equalsIgnoreCase(normalizeMode(mode));
    }

    public boolean isHttpMode() {
        return !isRpcMode();
    }

    private String normalizeMode(String value) {
        if (value == null || value.isBlank()) {
            return "http";
        }
        return value.trim().toLowerCase();
    }
}
