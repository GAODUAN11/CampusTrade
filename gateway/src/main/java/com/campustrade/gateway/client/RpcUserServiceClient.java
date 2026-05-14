package com.campustrade.gateway.client;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(prefix = "campus.remote", name = "mode", havingValue = "rpc")
public class RpcUserServiceClient extends BaseRemoteClient implements UserServiceClient {
    private final GatewayRemoteProperties properties;

    public RpcUserServiceClient(GatewayRemoteProperties properties,
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
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "getUserById", userId);
    }

    @Override
    public SellerProfileDTO getSellerProfile(Long userId) {
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "getSellerProfile", userId);
    }

    @Override
    public UserDTO register(RegisterRequest request) {
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "register", request);
    }

    @Override
    public LoginResponseDTO login(LoginRequest request) {
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "login", request);
    }

    @Override
    public void logout(String token) {
        callRpc(ServiceNames.USER_SERVICE, endpoint(), "logout", normalizeToken(token));
    }

    @Override
    public Long authenticate(String token) {
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
