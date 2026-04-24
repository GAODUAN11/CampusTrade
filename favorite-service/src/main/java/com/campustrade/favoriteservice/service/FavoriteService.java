package com.campustrade.favoriteservice.service;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.model.PageResponse;

public interface FavoriteService {
    FavoriteDTO addFavorite(Long userId, Long productId);

    void removeFavorite(Long userId, Long productId);

    boolean isFavorited(Long userId, Long productId);

    PageResponse<FavoriteDTO> listMyFavorites(Long userId, int pageNo, int pageSize);
}
