package com.campustrade.userservice.service.impl;

import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.enums.UserStatus;
import com.campustrade.userservice.entity.SellerProfileEntity;
import com.campustrade.userservice.entity.UserEntity;
import com.campustrade.userservice.repository.SellerProfileJpaRepository;
import com.campustrade.userservice.repository.UserJpaRepository;
import com.campustrade.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserJpaRepository userRepository;
    private final SellerProfileJpaRepository sellerProfileRepository;

    public UserServiceImpl(UserJpaRepository userRepository,
                           SellerProfileJpaRepository sellerProfileRepository) {
        this.userRepository = userRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }

    @Override
    public Optional<UserDTO> findUserById(Long userId) {
        return userRepository.findById(userId).map(this::toUserDTO);
    }

    @Override
    public Optional<SellerProfileDTO> findSellerProfileByUserId(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        Optional<SellerProfileEntity> profileOpt = sellerProfileRepository.findById(userId);
        if (userOpt.isEmpty() || profileOpt.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toSellerProfileDTO(userOpt.get(), profileOpt.get()));
    }

    @Override
    public Optional<UserStatus> findUserStatusById(Long userId) {
        return userRepository.findById(userId).map(UserEntity::getStatus);
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

    private SellerProfileDTO toSellerProfileDTO(UserEntity user, SellerProfileEntity profile) {
        SellerProfileDTO dto = new SellerProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setNickname(user.getNickname());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setSchool(user.getSchool());
        dto.setCampus(user.getCampus());
        dto.setCreditScore(profile.getCreditScore());
        dto.setProductCount(profile.getProductCount());
        dto.setSoldCount(profile.getSoldCount());
        dto.setOnline(profile.getOnline());
        dto.setStatus(user.getStatus());
        dto.setJoinedAt(profile.getJoinedAt());
        return dto;
    }
}
