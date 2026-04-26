package com.campustrade.gateway.client;

import com.campustrade.common.result.Result;
import com.campustrade.common.rpc.RpcClient;
import com.campustrade.common.rpc.RpcException;
import com.campustrade.common.rpc.protocol.JavaRpcSerializer;
import com.campustrade.common.rpc.protocol.RpcProtocol;
import com.campustrade.common.rpc.registry.RpcServiceInstance;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.exception.RemoteServiceException;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared helper for HTTP/RPC dual-mode remote invocation.
 */
public abstract class BaseRemoteClient {
    private final GatewayRemoteProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GatewayRpcServiceLocator serviceLocator;

    protected BaseRemoteClient(GatewayRemoteProperties properties,
                               RestTemplate restTemplate,
                               ObjectMapper objectMapper,
                               GatewayRpcServiceLocator serviceLocator) {
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.serviceLocator = serviceLocator;
    }

    protected boolean useRpc() {
        return properties.isRpcMode();
    }

    protected <T> T callRpc(String serviceName,
                            GatewayRemoteProperties.ServiceEndpoint endpoint,
                            String methodName,
                            Object... args) {
        List<RpcServiceInstance> candidates = serviceLocator.resolveCandidates(serviceName, endpoint);
        if (candidates.isEmpty()) {
            throw new RemoteServiceException("No available endpoint for RPC service: " + serviceName);
        }

        Exception lastError = null;
        for (RpcServiceInstance instance : candidates) {
            try {
                RpcClient client = new RpcClient(
                        instance.getHost(),
                        instance.getPort(),
                        Duration.ofMillis(endpoint.getTimeoutMs() == null ? 5000 : endpoint.getTimeoutMs()),
                        resolveProtocolVersion(instance, endpoint),
                        resolveSerializer(instance, endpoint)
                );
                return client.invoke(serviceName, methodName, args);
            } catch (RpcException ex) {
                lastError = ex;
            } catch (Exception ex) {
                lastError = ex;
            }
        }

        String message = lastError == null ? "unknown error" : lastError.getMessage();
        throw new RemoteServiceException(
                "RPC " + serviceName + "." + methodName + " failed after trying " + candidates.size()
                        + " endpoint(s): " + message,
                lastError
        );
    }

    protected <T> T callHttpForObject(HttpMethod method,
                                      GatewayRemoteProperties.ServiceEndpoint endpoint,
                                      String path,
                                      Object requestBody,
                                      Class<T> clazz) {
        Result<?> response = callHttpResult(method, endpoint, path, requestBody, Map.of(), Map.of());
        return convertData(response, clazz);
    }

    protected <T> T callHttpForType(HttpMethod method,
                                    GatewayRemoteProperties.ServiceEndpoint endpoint,
                                    String path,
                                    Object requestBody,
                                    TypeReference<T> typeReference) {
        Result<?> response = callHttpResult(method, endpoint, path, requestBody, Map.of(), Map.of());
        return convertData(response, typeReference);
    }

    protected <T> T callHttpForObject(HttpMethod method,
                                      GatewayRemoteProperties.ServiceEndpoint endpoint,
                                      String path,
                                      Object requestBody,
                                      Map<String, Object> pathVariables,
                                      Map<String, Object> queryParams,
                                      Class<T> clazz) {
        Result<?> response = callHttpResult(method, endpoint, path, requestBody, pathVariables, queryParams);
        return convertData(response, clazz);
    }

    protected <T> T callHttpForType(HttpMethod method,
                                    GatewayRemoteProperties.ServiceEndpoint endpoint,
                                    String path,
                                    Object requestBody,
                                    Map<String, Object> pathVariables,
                                    Map<String, Object> queryParams,
                                    TypeReference<T> typeReference) {
        Result<?> response = callHttpResult(method, endpoint, path, requestBody, pathVariables, queryParams);
        return convertData(response, typeReference);
    }

    protected void callHttpNoContent(HttpMethod method,
                                     GatewayRemoteProperties.ServiceEndpoint endpoint,
                                     String path,
                                     Object requestBody,
                                     Map<String, Object> pathVariables,
                                     Map<String, Object> queryParams) {
        callHttpResult(method, endpoint, path, requestBody, pathVariables, queryParams);
    }

    private Result<?> callHttpResult(HttpMethod method,
                                     GatewayRemoteProperties.ServiceEndpoint endpoint,
                                     String path,
                                     Object requestBody,
                                     Map<String, Object> pathVariables,
                                     Map<String, Object> queryParams) {
        validateBaseUrl(endpoint);
        String urlTemplate = buildUrl(endpoint.getBaseUrl(), path, queryParams);
        HttpEntity<?> entity = requestBody == null ? HttpEntity.EMPTY : new HttpEntity<>(requestBody);
        try {
            ResponseEntity<Result> response = restTemplate.exchange(
                    urlTemplate,
                    method,
                    entity,
                    Result.class,
                    sanitizeVariables(pathVariables)
            );
            Result<?> result = response.getBody();
            if (result == null) {
                throw new RemoteServiceException("HTTP result is empty for " + method + " " + urlTemplate);
            }
            if (!result.isSuccess()) {
                throw new RemoteServiceException(
                        "HTTP remote returned failure code="
                                + result.getCode()
                                + ", message="
                                + result.getMessage()
                );
            }
            return result;
        } catch (RemoteServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RemoteServiceException("HTTP remote call failed: " + method + " " + urlTemplate, ex);
        }
    }

    private String buildUrl(String baseUrl, String path, Map<String, Object> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path(path);
        if (queryParams != null) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                    continue;
                }
                builder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return builder.build(false).toUriString();
    }

    private Map<String, Object> sanitizeVariables(Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> sanitized = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                continue;
            }
            sanitized.put(entry.getKey(), entry.getValue());
        }
        return sanitized;
    }

    private void validateBaseUrl(GatewayRemoteProperties.ServiceEndpoint endpoint) {
        if (endpoint == null) {
            throw new RemoteServiceException("HTTP endpoint config is missing");
        }
        String baseUrl = endpoint.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new RemoteServiceException("HTTP base-url is missing in remote endpoint config");
        }
    }

    private <T> T convertData(Result<?> result, Class<T> clazz) {
        if (clazz == null || clazz == Void.class || clazz == Void.TYPE) {
            return null;
        }
        try {
            return objectMapper.convertValue(result.getData(), clazz);
        } catch (IllegalArgumentException ex) {
            throw new RemoteServiceException("Failed to parse HTTP result data to " + clazz.getSimpleName(), ex);
        }
    }

    private <T> T convertData(Result<?> result, TypeReference<T> typeReference) {
        try {
            return objectMapper.convertValue(result.getData(), typeReference);
        } catch (IllegalArgumentException ex) {
            throw new RemoteServiceException("Failed to parse HTTP result data", ex);
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

