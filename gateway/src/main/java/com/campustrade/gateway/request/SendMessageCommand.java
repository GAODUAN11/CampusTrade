package com.campustrade.gateway.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SendMessageCommand {
    private Long conversationId;
    private Long productId;

    @NotNull(message = "receiverId is required")
    private Long receiverId;

    @Size(max = 32, message = "messageType length must be <= 32")
    private String messageType = "TEXT";

    @NotBlank(message = "content cannot be blank")
    @Size(max = 1000, message = "content length must be <= 1000")
    private String content;

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

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

