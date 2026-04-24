package com.campustrade.gateway.vo;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;

public class FavoriteItemVO {
    private FavoriteDTO favorite;
    private ProductDTO product;
    private SellerProfileDTO seller;

    public FavoriteDTO getFavorite() {
        return favorite;
    }

    public void setFavorite(FavoriteDTO favorite) {
        this.favorite = favorite;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public SellerProfileDTO getSeller() {
        return seller;
    }

    public void setSeller(SellerProfileDTO seller) {
        this.seller = seller;
    }
}
