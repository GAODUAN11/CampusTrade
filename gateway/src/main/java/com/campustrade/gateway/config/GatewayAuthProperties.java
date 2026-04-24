package com.campustrade.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "campus.auth")
public class GatewayAuthProperties {
    private String tokenPrefix = "ctk_";
    private long expireSeconds = 86400L;
    private long rememberExpireSeconds = 604800L;

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public long getRememberExpireSeconds() {
        return rememberExpireSeconds;
    }

    public void setRememberExpireSeconds(long rememberExpireSeconds) {
        this.rememberExpireSeconds = rememberExpireSeconds;
    }
}
