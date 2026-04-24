package com.campustrade.userservice.service;

import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.enums.UserStatus;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> findUserById(Long userId);

    Optional<SellerProfileDTO> findSellerProfileByUserId(Long userId);

    Optional<UserStatus> findUserStatusById(Long userId);
}
