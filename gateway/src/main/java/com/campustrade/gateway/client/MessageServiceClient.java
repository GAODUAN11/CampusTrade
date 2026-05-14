package com.campustrade.gateway.client;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;

import java.util.List;

public interface MessageServiceClient {
    List<ConversationDTO> listConversations(Long userId);

    List<MessageDTO> listConversationMessages(Long conversationId, Long userId);

    Integer unreadCount(Long userId);

    MessageDTO sendMessage(Object requestBody);
}
