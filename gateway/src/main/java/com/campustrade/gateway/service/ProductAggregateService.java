package com.campustrade.gateway.service;

import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.gateway.client.FavoriteServiceClient;
import com.campustrade.gateway.client.ProductServiceClient;
import com.campustrade.gateway.client.UserServiceClient;
import com.campustrade.gateway.vo.ProductDetailPageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductAggregateService {
    private static final Logger log = LoggerFactory.getLogger(ProductAggregateService.class);

    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final FavoriteServiceClient favoriteServiceClient;

    public ProductAggregateService(ProductServiceClient productServiceClient,
                                   UserServiceClient userServiceClient,
                                   FavoriteServiceClient favoriteServiceClient) {
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
        this.favoriteServiceClient = favoriteServiceClient;
    }

    public ProductDetailPageVO aggregateProductDetail(Long productId, Long userId) {
        ProductDetailDTO productDetail = productServiceClient.getProductDetail(productId);

        Long sellerId = null;
        if (productDetail != null && productDetail.getProduct() != null) {
            sellerId = productDetail.getProduct().getSellerId();
        }

        SellerProfileDTO sellerProfile = null;
        if (sellerId != null) {
            sellerProfile = userServiceClient.getSellerProfile(sellerId);
        }

        boolean favorited = false;
        if (userId != null && userId > 0) {
            try {
                Boolean checkResult = favoriteServiceClient.checkFavorite(userId, productId);
                favorited = Boolean.TRUE.equals(checkResult);
            } catch (Exception ex) {
                // Degrade gracefully when favorite-service is unavailable.
                favorited = false;
                log.warn(
                        "favorite-service unavailable, fallback favorited=false, userId={}, productId={}, reason={}",
                        userId, productId, ex.getMessage()
                );
            }
        }

        ProductDetailPageVO vo = new ProductDetailPageVO();
        vo.setProductId(productId);
        vo.setCurrentUserId(userId);
        if (productDetail != null) {
            vo.setProduct(productDetail.getProduct());
            vo.setDescription(productDetail.getDescription());
            vo.setImageUrls(productDetail.getImageUrls());
            vo.setTags(productDetail.getTags());
        }
        vo.setSeller(sellerProfile != null ? sellerProfile : (productDetail == null ? null : productDetail.getSeller()));
        vo.setFavorited(favorited);
        return vo;
    }
}
