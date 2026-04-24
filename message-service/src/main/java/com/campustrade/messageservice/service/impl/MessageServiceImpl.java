package com.campustrade.messageservice.service.impl;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.common.result.ResultCode;
import com.campustrade.messageservice.entity.MessageConversationEntity;
import com.campustrade.messageservice.entity.MessageEntity;
import com.campustrade.messageservice.entity.MessageProductSnapshotEntity;
import com.campustrade.messageservice.exception.BusinessException;
import com.campustrade.messageservice.repository.MessageConversationJpaRepository;
import com.campustrade.messageservice.repository.MessageJpaRepository;
import com.campustrade.messageservice.repository.MessageProductSnapshotJpaRepository;
import com.campustrade.messageservice.request.SendMessageRequest;
import com.campustrade.messageservice.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageConversationJpaRepository conversationRepository;
    private final MessageJpaRepository messageRepository;
    private final MessageProductSnapshotJpaRepository productSnapshotRepository;

    public MessageServiceImpl(MessageConversationJpaRepository conversationRepository,
                              MessageJpaRepository messageRepository,
                              MessageProductSnapshotJpaRepository productSnapshotRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.productSnapshotRepository = productSnapshotRepository;
    }

    @Override
    public MessageDTO sendMessage(SendMessageRequest request) {
        validateSendRequest(request);

        MessageConversationEntity conversation = resolveConversation(request);
        verifyParticipant(conversation, request.getSenderId());
        verifyParticipant(conversation, request.getReceiverId());
        if (Objects.equals(request.getSenderId(), request.getReceiverId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "senderId and receiverId cannot be the same.");
        }

        LocalDateTime now = LocalDateTime.now();
        MessageEntity message = new MessageEntity();
        message.setConversationId(conversation.getConversationId());
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setMessageType(normalizeMessageType(request.getMessageType()));
        message.setContent(request.getContent().trim());
        message.setRead(Boolean.FALSE);
        message.setSentAt(now);
        MessageEntity saved = messageRepository.save(message);

        conversation.setLastMessagePreview(buildPreview(message.getContent()));
        conversation.setLastSenderId(message.getSenderId());
        conversation.setLastMessageTime(now);
        conversationRepository.save(conversation);

        return toMessageDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDTO> listConversations(Long userId) {
        validateUserId(userId);
        return conversationRepository.findByBuyerIdOrSellerId(userId, userId).stream()
                .sorted(Comparator.comparing(MessageConversationEntity::getLastMessageTime).reversed())
                .map(record -> toConversationDTO(record, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> listConversationMessages(Long conversationId, Long viewerUserId) {
        validateConversationId(conversationId);
        validateUserId(viewerUserId);

        MessageConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Conversation not found: " + conversationId));
        verifyParticipant(conversation, viewerUserId);

        messageRepository.markAsRead(conversationId, viewerUserId, LocalDateTime.now());
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int unreadCount(Long userId) {
        validateUserId(userId);
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    private MessageConversationEntity resolveConversation(SendMessageRequest request) {
        if (request.getConversationId() != null) {
            return conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Conversation not found: " + request.getConversationId()));
        }

        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "productId is required when conversationId is absent.");
        }

        return conversationRepository.findByProductAndParticipants(
                        request.getProductId(),
                        request.getSenderId(),
                        request.getReceiverId()
                )
                .orElseGet(() -> createConversation(
                        request.getProductId(),
                        request.getSenderId(),
                        request.getReceiverId()
                ));
    }

    private MessageConversationEntity createConversation(Long productId, Long buyerId, Long sellerId) {
        MessageProductSnapshotEntity snapshot = productSnapshotRepository.findById(productId)
                .orElseGet(() -> defaultSnapshot(productId));
        LocalDateTime now = LocalDateTime.now();

        MessageConversationEntity conversation = new MessageConversationEntity();
        conversation.setProductId(productId);
        conversation.setBuyerId(buyerId);
        conversation.setSellerId(sellerId);
        conversation.setProductTitle(snapshot.getTitle());
        conversation.setProductCoverImageUrl(snapshot.getCoverImageUrl());
        conversation.setLastMessagePreview("");
        conversation.setLastSenderId(null);
        conversation.setLastMessageTime(now);
        conversation.setArchived(Boolean.FALSE);
        conversation.setCreatedAt(now);
        conversation.setUpdatedAt(now);
        return conversationRepository.save(conversation);
    }

    private MessageProductSnapshotEntity defaultSnapshot(Long productId) {
        MessageProductSnapshotEntity snapshot = new MessageProductSnapshotEntity();
        snapshot.setProductId(productId);
        snapshot.setTitle("Unknown Product");
        snapshot.setCoverImageUrl("https://example.com/product/default.jpg");
        snapshot.setUpdatedAt(LocalDateTime.now());
        return snapshot;
    }

    private String normalizeMessageType(String messageType) {
        if (messageType == null || messageType.trim().isEmpty()) {
            return "TEXT";
        }
        return messageType.trim().toUpperCase();
    }

    private void verifyParticipant(MessageConversationEntity conversation, Long userId) {
        boolean participant = Objects.equals(conversation.getBuyerId(), userId) || Objects.equals(conversation.getSellerId(), userId);
        if (!participant) {
            throw new BusinessException(ResultCode.FORBIDDEN, "User is not a participant in this conversation.");
        }
    }

    private void validateSendRequest(SendMessageRequest request) {
        validateUserId(request.getSenderId());
        validateUserId(request.getReceiverId());
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "content cannot be blank.");
        }
    }

    private void validateConversationId(Long conversationId) {
        if (conversationId == null || conversationId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "conversationId is invalid.");
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "userId is invalid.");
        }
    }

    private String buildPreview(String content) {
        if (content == null) {
            return "";
        }
        String normalized = content.trim();
        return normalized.length() <= 40 ? normalized : normalized.substring(0, 40);
    }

    private ConversationDTO toConversationDTO(MessageConversationEntity record, Long currentUserId) {
        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId(record.getConversationId());
        dto.setProductId(record.getProductId());
        dto.setBuyerId(record.getBuyerId());
        dto.setSellerId(record.getSellerId());
        dto.setProductTitle(record.getProductTitle());
        dto.setProductCoverImageUrl(record.getProductCoverImageUrl());
        dto.setLastMessagePreview(record.getLastMessagePreview());
        dto.setLastSenderId(record.getLastSenderId());
        dto.setLastMessageTime(record.getLastMessageTime());
        dto.setUnreadCount(messageRepository.countByConversationIdAndReceiverIdAndReadFalse(record.getConversationId(), currentUserId));
        dto.setArchived(record.getArchived());
        return dto;
    }

    private MessageDTO toMessageDTO(MessageEntity record) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(record.getMessageId());
        dto.setConversationId(record.getConversationId());
        dto.setSenderId(record.getSenderId());
        dto.setReceiverId(record.getReceiverId());
        dto.setMessageType(record.getMessageType());
        dto.setContent(record.getContent());
        dto.setRead(record.getRead());
        dto.setSentAt(record.getSentAt());
        dto.setReadAt(record.getReadAt());
        return dto;
    }
}
