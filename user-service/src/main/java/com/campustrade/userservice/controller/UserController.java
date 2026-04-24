package com.campustrade.userservice.controller;

import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.common.enums.UserStatus;
import com.campustrade.common.result.Result;
import com.campustrade.userservice.app.UserQueryFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserQueryFacade userQueryFacade;

    public UserController(UserQueryFacade userQueryFacade) {
        this.userQueryFacade = userQueryFacade;
    }

    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable("id") Long userId) {
        return Result.success(userQueryFacade.getUserById(userId));
    }

    @GetMapping("/{id}/seller-profile")
    public Result<SellerProfileDTO> getSellerProfile(@PathVariable("id") Long userId) {
        return Result.success(userQueryFacade.getSellerProfileByUserId(userId));
    }

    @GetMapping("/{id}/status")
    public Result<UserStatus> getUserStatus(@PathVariable("id") Long userId) {
        return Result.success(userQueryFacade.getUserStatusById(userId));
    }
}
