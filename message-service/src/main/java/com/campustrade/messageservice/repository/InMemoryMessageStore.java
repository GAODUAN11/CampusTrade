package com.campustrade.messageservice.repository;

import com.campustrade.messageservice.repository.model.ConversationRecord;
import com.campustrade.messageservice.repository.model.MessageRecord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Mock storage for phase 1. Replaceable by database implementation later.
 */
@Repository
public class InMemoryMessageStore {
    private final Map<Long, ConversationRecord> conversationMap = new ConcurrentHashMap<>();
    private final Map<Long, List<MessageRecord>> conversationMessagesMap = new ConcurrentHashMap<>();
    private final Map<Long, ProductSnapshot> productSnapshotMap = new ConcurrentHashMap<>();

    private final AtomicLong conversationIdGenerator = new AtomicLong(40000);
    private final AtomicLong messageIdGenerator = new AtomicLong(80000);

    @PostConstruct
    public void initMockData() {
        productSnapshotMap.put(20001L, new ProductSnapshot(20001L, "iPad 9 64G", "https://example.com/product/ipad-9.jpg"));
        productSnapshotMap.put(20002L, new ProductSnapshot(20002L, "Road Bike", "https://example.com/product/road-bike.jpg"));
        productSnapshotMap.put(20004L, new ProductSnapshot(20004L, "Desk Lamp", "https://example.com/product/desk-lamp.jpg"));

        ConversationRecord c1 = createConversation(20001L, 10001L, 20001L);
        addMessage(c1.getConversationId(), 10001L, 20001L, "TEXT", "Hi, is this still available?");
        addMessage(c1.getConversationId(), 20001L, 10001L, "TEXT", "Yes, still available.");

        ConversationRecord c2 = createConversation(20004L, 10002L, 20002L);
        addMessage(c2.getConversationId(), 10002L, 20002L, "TEXT", "Can you lower the price?");
    }

    public List<ConversationRecord> findConversationsByUserId(Long userId) {
        return conversationMap.values().stream()
                .filter(conversation -> userId.equals(conversation.getBuyerId()) || userId.equals(conversation.getSellerId()))
                .map(this::copyConversation)
                .collect(Collectors.toList());
    }

    public Optional<ConversationRecord> findConversationById(Long conversationId) {
        return Optional.ofNullable(conversationMap.get(conversationId)).map(this::copyConversation);
    }

    public Optional<ConversationRecord> findConversationByProductAndParticipants(Long productId, Long senderId, Long receiverId) {
        return conversationMap.values().stream()
                .filter(c -> productId.equals(c.getProductId()))
                .filter(c -> isParticipantsMatch(c, senderId, receiverId))
                .findFirst()
                .map(this::copyConversation);
    }

    public ConversationRecord createConversation(Long productId, Long buyerId, Long sellerId) {
        ProductSnapshot snapshot = productSnapshotMap.getOrDefault(productId, ProductSnapshot.defaultSnapshot(productId));
        LocalDateTime now = LocalDateTime.now();

        ConversationRecord conversation = new ConversationRecord();
        conversation.setConversationId(conversationIdGenerator.incrementAndGet());
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

        conversationMap.put(conversation.getConversationId(), copyConversation(conversation));
        conversationMessagesMap.put(conversation.getConversationId(), new ArrayList<>());
        return copyConversation(conversation);
    }

    public MessageRecord addMessage(Long conversationId, Long senderId, Long receiverId, String messageType, String content) {
        ConversationRecord conversation = conversationMap.get(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation does not exist: " + conversationId);
        }

        LocalDateTime now = LocalDateTime.now();
        MessageRecord message = new MessageRecord();
        message.setMessageId(messageIdGenerator.incrementAndGet());
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setRead(Boolean.FALSE);
        message.setSentAt(now);
        message.setReadAt(null);

        List<MessageRecord> list = conversationMessagesMap.computeIfAbsent(conversationId, key -> new ArrayList<>());
        list.add(copyMessage(message));

        conversation.setLastMessagePreview(buildPreview(content));
        conversation.setLastSenderId(senderId);
        conversation.setLastMessageTime(now);
        conversation.setUpdatedAt(now);
        conversationMap.put(conversationId, copyConversation(conversation));

        return copyMessage(message);
    }

    public List<MessageRecord> findMessagesByConversationId(Long conversationId) {
        List<MessageRecord> list = conversationMessagesMap.getOrDefault(conversationId, new ArrayList<>());
        return list.stream()
                .sorted(Comparator.comparing(MessageRecord::getSentAt))
                .map(this::copyMessage)
                .collect(Collectors.toList());
    }

    public void markConversationAsRead(Long conversationId, Long readerUserId) {
        List<MessageRecord> list = conversationMessagesMap.get(conversationId);
        if (list == null || list.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (MessageRecord message : list) {
            if (readerUserId.equals(message.getReceiverId()) && !Boolean.TRUE.equals(message.getRead())) {
                message.setRead(Boolean.TRUE);
                message.setReadAt(now);
            }
        }
    }

    public int countUnreadByUserId(Long userId) {
        int total = 0;
        for (List<MessageRecord> list : conversationMessagesMap.values()) {
            for (MessageRecord message : list) {
                if (userId.equals(message.getReceiverId()) && !Boolean.TRUE.equals(message.getRead())) {
                    total++;
                }
            }
        }
        return total;
    }

    public int countUnreadByConversation(Long conversationId, Long userId) {
        List<MessageRecord> list = conversationMessagesMap.getOrDefault(conversationId, new ArrayList<>());
        int total = 0;
        for (MessageRecord message : list) {
            if (userId.equals(message.getReceiverId()) && !Boolean.TRUE.equals(message.getRead())) {
                total++;
            }
        }
        return total;
    }

    private boolean isParticipantsMatch(ConversationRecord conversation, Long userA, Long userB) {
        boolean direct = userA.equals(conversation.getBuyerId()) && userB.equals(conversation.getSellerId());
        boolean reverse = userA.equals(conversation.getSellerId()) && userB.equals(conversation.getBuyerId());
        return direct || reverse;
    }

    private String buildPreview(String content) {
        if (content == null) {
            return "";
        }
        String normalized = content.trim();
        return normalized.length() <= 40 ? normalized : normalized.substring(0, 40);
    }

    private ConversationRecord copyConversation(ConversationRecord source) {
        ConversationRecord target = new ConversationRecord();
        target.setConversationId(source.getConversationId());
        target.setProductId(source.getProductId());
        target.setBuyerId(source.getBuyerId());
        target.setSellerId(source.getSellerId());
        target.setProductTitle(source.getProductTitle());
        target.setProductCoverImageUrl(source.getProductCoverImageUrl());
        target.setLastMessagePreview(source.getLastMessagePreview());
        target.setLastSenderId(source.getLastSenderId());
        target.setLastMessageTime(source.getLastMessageTime());
        target.setArchived(source.getArchived());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
        return target;
    }

    private MessageRecord copyMessage(MessageRecord source) {
        MessageRecord target = new MessageRecord();
        target.setMessageId(source.getMessageId());
        target.setConversationId(source.getConversationId());
        target.setSenderId(source.getSenderId());
        target.setReceiverId(source.getReceiverId());
        target.setMessageType(source.getMessageType());
        target.setContent(source.getContent());
        target.setRead(source.getRead());
        target.setSentAt(source.getSentAt());
        target.setReadAt(source.getReadAt());
        return target;
    }

    private static class ProductSnapshot {
        private final Long productId;
        private final String title;
        private final String coverImageUrl;

        private ProductSnapshot(Long productId, String title, String coverImageUrl) {
            this.productId = productId;
            this.title = title;
            this.coverImageUrl = coverImageUrl;
        }

        public Long getProductId() {
            return productId;
        }

        public String getTitle() {
            return title;
        }

        public String getCoverImageUrl() {
            return coverImageUrl;
        }

        public static ProductSnapshot defaultSnapshot(Long productId) {
            return new ProductSnapshot(productId, "Unknown Product", "https://example.com/product/default.jpg");
        }
    }
}
