package com.campustrade.messageservice.repository;

import com.campustrade.messageservice.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByConversationIdOrderBySentAtAsc(Long conversationId);

    int countByReceiverIdAndReadFalse(Long receiverId);

    int countByConversationIdAndReceiverIdAndReadFalse(Long conversationId, Long receiverId);

    @Modifying
    @Query("""
            update MessageEntity m
               set m.read = true,
                   m.readAt = :readAt
             where m.conversationId = :conversationId
               and m.receiverId = :receiverId
               and m.read = false
            """)
    int markAsRead(@Param("conversationId") Long conversationId,
                   @Param("receiverId") Long receiverId,
                   @Param("readAt") LocalDateTime readAt);
}
