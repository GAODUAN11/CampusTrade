package com.campustrade.messageservice.service;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.messageservice.request.SendMessageRequest;

import java.util.List;

public interface MessageService {
    MessageDTO sendMessage(SendMessageRequest request);

    List<ConversationDTO> listConversations(Long userId);

    List<MessageDTO> listConversationMessages(Long conversationId, Long viewerUserId);

    int unreadCount(Long userId);
}
