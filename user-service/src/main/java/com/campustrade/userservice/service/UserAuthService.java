package com.campustrade.userservice.service;

import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;

public interface UserAuthService {
    UserDTO register(RegisterRequest request);

    LoginResponseDTO login(LoginRequest request);

    void logout(String token);

    Long authenticate(String token);
}
