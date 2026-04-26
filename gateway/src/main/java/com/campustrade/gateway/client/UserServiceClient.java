package com.campustrade.gateway.client;

import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public UserServiceClient(GatewayRemoteProperties properties,
                             RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getUserService();
    }

    public UserDTO getUserById(Long userId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/users/{id}",
                    null,
                    Map.of("id", userId),
                    Map.of(),
                    UserDTO.class
            );
        }
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "getUserById", userId);
    }

    public SellerProfileDTO getSellerProfile(Long userId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/users/{id}/seller-profile",
                    null,
                    Map.of("id", userId),
                    Map.of(),
                    SellerProfileDTO.class
            );
        }
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "getSellerProfile", userId);
    }

    public UserDTO register(RegisterRequest request) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.POST,
                    endpoint(),
                    "/api/auth/register",
                    request,
                    UserDTO.class
            );
        }
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "register", request);
    }

    public LoginResponseDTO login(LoginRequest request) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.POST,
                    endpoint(),
                    "/api/auth/login",
                    request,
                    LoginResponseDTO.class
            );
        }
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "login", request);
    }

    public void logout(String token) {
        if (!useRpc()) {
            Map<String, Object> query = new HashMap<>();
            String normalizedToken = normalizeToken(token);
            if (normalizedToken != null) {
                query.put("token", normalizedToken);
            }
            callHttpNoContent(
                    HttpMethod.POST,
                    endpoint(),
                    "/api/auth/logout",
                    null,
                    Map.of(),
                    query
            );
            return;
        }
        callRpc(ServiceNames.USER_SERVICE, endpoint(), "logout", normalizeToken(token));
    }

    public Long authenticate(String token) {
        if (!useRpc()) {
            Map<String, Object> query = new HashMap<>();
            String normalizedToken = normalizeToken(token);
            if (normalizedToken != null) {
                query.put("token", normalizedToken);
            }
            return callHttpForObject(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/auth/verify",
                    null,
                    Map.of(),
                    query,
                    Long.class
            );
        }
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "authenticate", normalizeToken(token));
    }

    private String normalizeToken(String token) {
        if (token == null) {
            return null;
        }
        String normalized = token.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
