package com.campustrade.userservice.controller;

import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;
import com.campustrade.common.result.Result;
import com.campustrade.userservice.service.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userAuthService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userAuthService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "token", required = false) String token
    ) {
        userAuthService.logout(resolveToken(authorization, token));
        return Result.success();
    }

    @GetMapping("/verify")
    public Result<Long> verify(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "token", required = false) String token
    ) {
        return Result.success(userAuthService.authenticate(resolveToken(authorization, token)));
    }

    private String resolveToken(String authorization, String token) {
        if (authorization != null) {
            String value = authorization.trim();
            if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
                return value.substring(7).trim();
            }
            if (!value.isEmpty()) {
                return value;
            }
        }
        if (token != null && !token.trim().isEmpty()) {
            return token.trim();
        }
        return null;
    }
}
