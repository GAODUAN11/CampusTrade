package com.campustrade.messageservice.repository.model;

import java.time.LocalDateTime;

public class ConversationRecord {
    private Long conversationId;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private String productTitle;
    private String productCoverImageUrl;
    private String lastMessagePreview;
    private Long lastSenderId;
    private LocalDateTime lastMessageTime;
    private Boolean archived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductCoverImageUrl() {
        return productCoverImageUrl;
    }

    public void setProductCoverImageUrl(String productCoverImageUrl) {
        this.productCoverImageUrl = productCoverImageUrl;
    }

    public String getLastMessagePreview() {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }

    public Long getLastSenderId() {
        return lastSenderId;
    }

    public void setLastSenderId(Long lastSenderId) {
        this.lastSenderId = lastSenderId;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
