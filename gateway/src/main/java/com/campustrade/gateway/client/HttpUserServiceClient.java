package com.campustrade.gateway.client;

import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(
        prefix = "campus.remote",
        name = "mode",
        havingValue = "http",
        matchIfMissing = true
)
public class HttpUserServiceClient extends BaseRemoteClient implements UserServiceClient {
    private final GatewayRemoteProperties properties;

    public HttpUserServiceClient(GatewayRemoteProperties properties,
                                 RestTemplate restTemplate,
                                 ObjectMapper objectMapper,
                                 GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getUserService();
    }

    @Override
    public UserDTO getUserById(Long userId) {
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

    @Override
    public SellerProfileDTO getSellerProfile(Long userId) {
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

    @Override
    public UserDTO register(RegisterRequest request) {
        return callHttpForObject(
                HttpMethod.POST,
                endpoint(),
                "/api/auth/register",
                request,
                UserDTO.class
        );
    }

    @Override
    public LoginResponseDTO login(LoginRequest request) {
        return callHttpForObject(
                HttpMethod.POST,
                endpoint(),
                "/api/auth/login",
                request,
                LoginResponseDTO.class
        );
    }

    @Override
    public void logout(String token) {
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
    }

    @Override
    public Long authenticate(String token) {
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

    private String normalizeToken(String token) {
        if (token == null) {
            return null;
        }
        String normalized = token.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
