package com.campustrade.gateway.vo;

import java.util.List;

public class MessageInboxVO {
    private Long userId;
    private Integer unreadCount;
    private List<ConversationItemVO> conversations;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public List<ConversationItemVO> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationItemVO> conversations) {
        this.conversations = conversations;
    }
}
