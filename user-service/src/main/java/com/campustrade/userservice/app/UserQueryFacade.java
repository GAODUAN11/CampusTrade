package com.campustrade.userservice.app;

import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.enums.UserStatus;
import com.campustrade.common.result.ResultCode;
import com.campustrade.userservice.exception.BusinessException;
import com.campustrade.userservice.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserQueryFacade {
    private final UserService userService;

    public UserQueryFacade(UserService userService) {
        this.userService = userService;
    }

    public UserDTO getUserById(Long userId) {
        validateUserId(userId);
        return userService.findUserById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "User not found: " + userId));
    }

    public SellerProfileDTO getSellerProfileByUserId(Long userId) {
        validateUserId(userId);
        return userService.findSellerProfileByUserId(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Seller profile not found for user: " + userId));
    }

    public UserStatus getUserStatusById(Long userId) {
        validateUserId(userId);
        return userService.findUserStatusById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "User status not found: " + userId));
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Invalid user id");
        }
    }
}
