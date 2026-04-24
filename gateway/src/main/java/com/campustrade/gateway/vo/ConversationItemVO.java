package com.campustrade.gateway.vo;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.user.UserDTO;

public class ConversationItemVO {
    private ConversationDTO conversation;
    private Long counterpartUserId;
    private UserDTO counterpartUser;

    public ConversationDTO getConversation() {
        return conversation;
    }

    public void setConversation(ConversationDTO conversation) {
        this.conversation = conversation;
    }

    public Long getCounterpartUserId() {
        return counterpartUserId;
    }

    public void setCounterpartUserId(Long counterpartUserId) {
        this.counterpartUserId = counterpartUserId;
    }

    public UserDTO getCounterpartUser() {
        return counterpartUser;
    }

    public void setCounterpartUser(UserDTO counterpartUser) {
        this.counterpartUser = counterpartUser;
    }
}
