package com.campustrade.gateway.service;

import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;
import com.campustrade.gateway.client.UserServiceClient;
import com.campustrade.gateway.vo.LoginResponseVO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserServiceClient userServiceClient;

    public AuthService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public UserDTO register(RegisterRequest request) {
        return userServiceClient.register(request);
    }

    public LoginResponseVO login(LoginRequest request) {
        LoginResponseDTO login = userServiceClient.login(request);
        LoginResponseVO response = new LoginResponseVO();
        response.setTokenType(login.getTokenType());
        response.setAccessToken(login.getAccessToken());
        response.setExpiresInSeconds(login.getExpiresInSeconds());
        response.setExpiresAt(login.getExpiresAt());
        response.setUser(login.getUser());
        return response;
    }

    public void logout(String token) {
        userServiceClient.logout(token);
    }

    public Long authenticate(String token) {
        return userServiceClient.authenticate(token);
    }
}
