package com.campustrade.gateway.service;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.common.dto.user.UserDTO;
import com.campustrade.gateway.client.MessageServiceClient;
import com.campustrade.gateway.client.UserServiceClient;
import com.campustrade.gateway.request.SendMessageCommand;
import com.campustrade.gateway.vo.ConversationItemVO;
import com.campustrade.gateway.vo.MessageInboxVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MessageAggregateService {
    private final MessageServiceClient messageServiceClient;
    private final UserServiceClient userServiceClient;

    public MessageAggregateService(MessageServiceClient messageServiceClient, UserServiceClient userServiceClient) {
        this.messageServiceClient = messageServiceClient;
        this.userServiceClient = userServiceClient;
    }

    public MessageInboxVO myInbox(Long userId) {
        Integer unread = messageServiceClient.unreadCount(userId);
        List<ConversationDTO> conversations = messageServiceClient.listConversations(userId);

        List<ConversationItemVO> items = new ArrayList<>();
        for (ConversationDTO conversation : conversations) {
            ConversationItemVO item = new ConversationItemVO();
            item.setConversation(conversation);

            Long counterpartId = resolveCounterpart(conversation, userId);
            item.setCounterpartUserId(counterpartId);

            if (counterpartId != null) {
                try {
                    UserDTO counterpart = userServiceClient.getUserById(counterpartId);
                    item.setCounterpartUser(counterpart);
                } catch (Exception ignored) {
                    // Keep conversation available even if user lookup fails.
                }
            }
            items.add(item);
        }

        MessageInboxVO inbox = new MessageInboxVO();
        inbox.setUserId(userId);
        inbox.setUnreadCount(unread == null ? 0 : unread);
        inbox.setConversations(items);
        return inbox;
    }

    public List<MessageDTO> conversationMessages(Long conversationId, Long userId) {
        return messageServiceClient.listConversationMessages(conversationId, userId);
    }

    public MessageDTO sendMessage(Long userId, SendMessageCommand request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("conversationId", request.getConversationId());
        payload.put("productId", request.getProductId());
        payload.put("senderId", userId);
        payload.put("receiverId", request.getReceiverId());
        payload.put("messageType", request.getMessageType());
        payload.put("content", request.getContent());
        return messageServiceClient.sendMessage(payload);
    }

    private Long resolveCounterpart(ConversationDTO conversation, Long currentUserId) {
        if (Objects.equals(conversation.getBuyerId(), currentUserId)) {
            return conversation.getSellerId();
        }
        if (Objects.equals(conversation.getSellerId(), currentUserId)) {
            return conversation.getBuyerId();
        }
        return null;
    }
}
