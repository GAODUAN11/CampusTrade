package com.campustrade.gateway.vo;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;

import java.util.List;

public class ProductDetailPageVO {
    private Long productId;
    private Long currentUserId;
    private ProductDTO product;
    private SellerProfileDTO seller;
    private String description;
    private List<String> imageUrls;
    private List<String> tags;
    private Boolean favorited;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }
}
