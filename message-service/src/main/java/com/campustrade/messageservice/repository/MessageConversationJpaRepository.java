package com.campustrade.messageservice.repository;

import com.campustrade.messageservice.entity.MessageConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageConversationJpaRepository extends JpaRepository<MessageConversationEntity, Long> {
    List<MessageConversationEntity> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);

    @Query("""
            select c from MessageConversationEntity c
            where c.productId = :productId
              and (
                (c.buyerId = :userA and c.sellerId = :userB)
                or
                (c.buyerId = :userB and c.sellerId = :userA)
              )
            """)
    Optional<MessageConversationEntity> findByProductAndParticipants(@Param("productId") Long productId,
                                                                      @Param("userA") Long userA,
                                                                      @Param("userB") Long userB);
}
