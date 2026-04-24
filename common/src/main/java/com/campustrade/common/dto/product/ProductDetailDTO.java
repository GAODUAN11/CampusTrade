package com.campustrade.common.dto.product;

import com.campustrade.common.dto.user.SellerProfileDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Product detail aggregate object used by gateway and product-service.
 */
public class ProductDetailDTO implements Serializable {
    private ProductDTO product;
    private SellerProfileDTO seller;
    private String description;
    private List<String> imageUrls;
    private List<String> tags;
    private Boolean favoritedByCurrentUser;

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

    public Boolean getFavoritedByCurrentUser() {
        return favoritedByCurrentUser;
    }

    public void setFavoritedByCurrentUser(Boolean favoritedByCurrentUser) {
        this.favoritedByCurrentUser = favoritedByCurrentUser;
    }
}
