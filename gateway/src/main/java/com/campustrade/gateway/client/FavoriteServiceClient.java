package com.campustrade.gateway.client;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.model.PageResponse;

public interface FavoriteServiceClient {
    Boolean checkFavorite(Long userId, Long productId);

    PageResponse<FavoriteDTO> listMyFavorites(Long userId, int pageNo, int pageSize);

    FavoriteDTO addFavorite(Long userId, Long productId);

    void removeFavorite(Long userId, Long productId);
}
