package com.campustrade.gateway.controller;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.result.Result;
import com.campustrade.gateway.service.FavoriteAggregateService;
import com.campustrade.gateway.security.CurrentUserId;
import com.campustrade.gateway.vo.FavoriteItemVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/favorites")
public class FavoriteAggregateController {
    private final FavoriteAggregateService favoriteAggregateService;

    public FavoriteAggregateController(FavoriteAggregateService favoriteAggregateService) {
        this.favoriteAggregateService = favoriteAggregateService;
    }

    @GetMapping
    public Result<PageResponse<FavoriteItemVO>> myFavorites(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(favoriteAggregateService.myFavorites(userId, pageNo, pageSize));
    }

    @PostMapping("/{productId}")
    public Result<FavoriteDTO> addFavorite(@CurrentUserId Long userId, @PathVariable Long productId) {
        return Result.success(favoriteAggregateService.addFavorite(userId, productId));
    }

    @DeleteMapping("/{productId}")
    public Result<Void> removeFavorite(@CurrentUserId Long userId, @PathVariable Long productId) {
        favoriteAggregateService.removeFavorite(userId, productId);
        return Result.success();
    }
}
