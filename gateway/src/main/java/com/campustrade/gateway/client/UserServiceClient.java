package com.campustrade.gateway.client;

import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public UserServiceClient(GatewayRemoteProperties properties,
                             GatewayRpcServiceLocator serviceLocator) {
        super(serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getUserService();
    }

    public UserDTO getUserById(Long userId) {
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "getUserById", userId);
    }

    public SellerProfileDTO getSellerProfile(Long userId) {
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "getSellerProfile", userId);
    }

    public UserDTO register(RegisterRequest request) {
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "register", request);
    }

    public LoginResponseDTO login(LoginRequest request) {
        return callRpc(ServiceNames.USER_SERVICE, endpoint(), "login", request);
    }

    public void logout(String token) {
        callRpc(ServiceNames.USER_SERVICE, endpoint(), "logout", normalizeToken(token));
    }

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
