package com.campustrade.userservice.service.impl;

import com.campustrade.common.dto.auth.LoginResponseDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.enums.UserStatus;
import com.campustrade.common.request.auth.LoginRequest;
import com.campustrade.common.request.auth.RegisterRequest;
import com.campustrade.common.result.ResultCode;
import com.campustrade.userservice.config.UserAuthProperties;
import com.campustrade.userservice.entity.SellerProfileEntity;
import com.campustrade.userservice.entity.UserCredentialEntity;
import com.campustrade.userservice.entity.UserEntity;
import com.campustrade.userservice.entity.UserSessionEntity;
import com.campustrade.userservice.exception.BusinessException;
import com.campustrade.userservice.repository.SellerProfileJpaRepository;
import com.campustrade.userservice.repository.UserCredentialJpaRepository;
import com.campustrade.userservice.repository.UserJpaRepository;
import com.campustrade.userservice.repository.UserSessionJpaRepository;
import com.campustrade.userservice.security.PasswordHasher;
import com.campustrade.userservice.service.UserAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserAuthServiceImpl implements UserAuthService {
    private final UserJpaRepository userRepository;
    private final SellerProfileJpaRepository sellerProfileRepository;
    private final UserCredentialJpaRepository userCredentialRepository;
    private final UserSessionJpaRepository userSessionRepository;
    private final PasswordHasher passwordHasher;
    private final UserAuthProperties authProperties;

    public UserAuthServiceImpl(UserJpaRepository userRepository,
                               SellerProfileJpaRepository sellerProfileRepository,
                               UserCredentialJpaRepository userCredentialRepository,
                               UserSessionJpaRepository userSessionRepository,
                               PasswordHasher passwordHasher,
                               UserAuthProperties authProperties) {
        this.userRepository = userRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.userSessionRepository = userSessionRepository;
        this.passwordHasher = passwordHasher;
        this.authProperties = authProperties;
    }

    @Override
    public UserDTO register(RegisterRequest request) {
        validateRegisterRequest(request);
        checkUniqueConstraints(request);

        UserEntity user = new UserEntity();
        user.setUserId(nextUserId());
        user.setUsername(request.getUsername().trim());
        user.setNickname(request.getNickname().trim());
        user.setPhone(normalizeNullable(request.getPhone()));
        user.setEmail(normalizeNullable(request.getEmail()));
        user.setSchool(normalizeNullable(request.getSchool()));
        user.setCampus(normalizeNullable(request.getCampus()));
        user.setVerified(Boolean.FALSE);
        user.setStatus(UserStatus.ACTIVE);
        user.setAvatarUrl(defaultAvatar(user.getUsername()));
        user.setBio("");
        UserEntity saved = userRepository.save(user);

        SellerProfileEntity sellerProfile = new SellerProfileEntity();
        sellerProfile.setUserId(saved.getUserId());
        sellerProfileRepository.save(sellerProfile);

        UserCredentialEntity credential = new UserCredentialEntity();
        credential.setUserId(saved.getUserId());
        credential.setPasswordHash(passwordHasher.hash(request.getPassword()));
        userCredentialRepository.save(credential);

        return toUserDTO(saved);
    }

    @Override
    public LoginResponseDTO login(LoginRequest request) {
        LocalDateTime now = LocalDateTime.now();
        userSessionRepository.deleteByExpiresAtBefore(now);

        UserEntity user = findByAccount(request.getAccount())
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "Account or password is incorrect."));

        if (user.getStatus() == UserStatus.BANNED || user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(ResultCode.FORBIDDEN, "This account is not allowed to login.");
        }

        UserCredentialEntity credential = userCredentialRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "Account or password is incorrect."));

        if (!passwordHasher.matches(request.getPassword(), credential.getPasswordHash())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Account or password is incorrect.");
        }

        long expiresInSeconds = Boolean.TRUE.equals(request.getRememberMe())
                ? authProperties.getRememberExpireSeconds()
                : authProperties.getExpireSeconds();
        LocalDateTime expiresAt = now.plusSeconds(expiresInSeconds);
        String token = generateAccessToken();

        UserSessionEntity session = new UserSessionEntity();
        session.setToken(token);
        session.setUserId(user.getUserId());
        session.setRememberMe(Boolean.TRUE.equals(request.getRememberMe()));
        session.setIssuedAt(now);
        session.setExpiresAt(expiresAt);
        userSessionRepository.save(session);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setTokenType("Bearer");
        response.setAccessToken(token);
        response.setExpiresInSeconds(expiresInSeconds);
        response.setExpiresAt(expiresAt);
        response.setUser(toUserDTO(user));
        return response;
    }

    @Override
    public void logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }
        userSessionRepository.deleteById(token.trim());
    }

    @Override
    public Long authenticate(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Login required.");
        }

        LocalDateTime now = LocalDateTime.now();
        userSessionRepository.deleteByExpiresAtBefore(now);

        UserSessionEntity session = userSessionRepository.findById(token.trim())
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "Login has expired, please login again."));
        if (session.getExpiresAt().isBefore(now)) {
            userSessionRepository.deleteById(token.trim());
            throw new BusinessException(ResultCode.UNAUTHORIZED, "Login has expired, please login again.");
        }
        return session.getUserId();
    }

    private Optional<UserEntity> findByAccount(String account) {
        if (account == null || account.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalized = account.trim();
        return userRepository.findByUsernameIgnoreCase(normalized)
                .or(() -> userRepository.findByPhone(normalized))
                .or(() -> userRepository.findByEmailIgnoreCase(normalized));
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (!Boolean.TRUE.equals(request.getAgreeTerms())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "You must agree to terms before registration.");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Password and confirmPassword do not match.");
        }
    }

    private void checkUniqueConstraints(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername().trim())) {
            throw new BusinessException(ResultCode.CONFLICT, "Username already exists.");
        }

        String phone = normalizeNullable(request.getPhone());
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new BusinessException(ResultCode.CONFLICT, "Phone already exists.");
        }

        String email = normalizeNullable(request.getEmail());
        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(ResultCode.CONFLICT, "Email already exists.");
        }
    }

    private UserDTO toUserDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setUserId(entity.getUserId());
        dto.setUsername(entity.getUsername());
        dto.setNickname(entity.getNickname());
        dto.setAvatarUrl(entity.getAvatarUrl());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setSchool(entity.getSchool());
        dto.setCampus(entity.getCampus());
        dto.setBio(entity.getBio());
        dto.setVerified(entity.getVerified());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String defaultAvatar(String username) {
        return "https://example.com/avatar/" + username + ".png";
    }

    private synchronized Long nextUserId() {
        Long currentMax = userRepository.findMaxUserId();
        return (currentMax == null ? 10001L : currentMax + 1L);
    }

    private String generateAccessToken() {
        String randomPart = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(randomPart.getBytes(StandardCharsets.UTF_8));
        return authProperties.getTokenPrefix() + encoded;
    }
}
