package com.campustrade.favoriteservice.controller;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.result.Result;
import com.campustrade.favoriteservice.service.FavoriteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public Result<FavoriteDTO> addFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        return Result.success(favoriteService.addFavorite(userId, productId));
    }

    @DeleteMapping
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        favoriteService.removeFavorite(userId, productId);
        return Result.success();
    }

    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        return Result.success(favoriteService.isFavorited(userId, productId));
    }

    @GetMapping("/my")
    public Result<PageResponse<FavoriteDTO>> myFavorites(@RequestParam Long userId,
                                                         @RequestParam(defaultValue = "1") int pageNo,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(favoriteService.listMyFavorites(userId, pageNo, pageSize));
    }
}
