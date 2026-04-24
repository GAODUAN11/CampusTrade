package com.campustrade.gateway.service;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.gateway.client.FavoriteServiceClient;
import com.campustrade.gateway.client.ProductServiceClient;
import com.campustrade.gateway.client.UserServiceClient;
import com.campustrade.gateway.vo.FavoriteItemVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteAggregateService {
    private final FavoriteServiceClient favoriteServiceClient;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    public FavoriteAggregateService(FavoriteServiceClient favoriteServiceClient,
                                    ProductServiceClient productServiceClient,
                                    UserServiceClient userServiceClient) {
        this.favoriteServiceClient = favoriteServiceClient;
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
    }

    public PageResponse<FavoriteItemVO> myFavorites(Long userId, int pageNo, int pageSize) {
        PageResponse<FavoriteDTO> favoritePage = favoriteServiceClient.listMyFavorites(userId, pageNo, pageSize);
        List<FavoriteItemVO> records = new ArrayList<>();
        if (favoritePage.getRecords() != null) {
            for (FavoriteDTO favorite : favoritePage.getRecords()) {
                records.add(toItemVO(favorite));
            }
        }
        return PageResponse.of(favoritePage.getTotal(), favoritePage.getPageNum(), favoritePage.getPageSize(), records);
    }

    public FavoriteDTO addFavorite(Long userId, Long productId) {
        return favoriteServiceClient.addFavorite(userId, productId);
    }

    public void removeFavorite(Long userId, Long productId) {
        favoriteServiceClient.removeFavorite(userId, productId);
    }

    private FavoriteItemVO toItemVO(FavoriteDTO favorite) {
        FavoriteItemVO item = new FavoriteItemVO();
        item.setFavorite(favorite);

        try {
            ProductDetailDTO detail = productServiceClient.getProductDetail(favorite.getProductId());
            if (detail != null) {
                item.setProduct(detail.getProduct());
                Long sellerId = detail.getProduct() == null ? null : detail.getProduct().getSellerId();
                if (sellerId != null) {
                    try {
                        SellerProfileDTO seller = userServiceClient.getSellerProfile(sellerId);
                        item.setSeller(seller);
                    } catch (Exception ignored) {
                        item.setSeller(detail.getSeller());
                    }
                } else {
                    item.setSeller(detail.getSeller());
                }
            }
        } catch (Exception ignored) {
            // Keep favorite basic info when product-service has partial failure.
        }
        return item;
    }
}
