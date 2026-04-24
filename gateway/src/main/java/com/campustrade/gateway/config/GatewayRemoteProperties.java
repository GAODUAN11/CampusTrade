package com.campustrade.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "campus.remote")
public class GatewayRemoteProperties {
    private ServiceEndpoint userService = new ServiceEndpoint();
    private ServiceEndpoint productService = new ServiceEndpoint();
    private ServiceEndpoint favoriteService = new ServiceEndpoint();
    private ServiceEndpoint messageService = new ServiceEndpoint();

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
}
