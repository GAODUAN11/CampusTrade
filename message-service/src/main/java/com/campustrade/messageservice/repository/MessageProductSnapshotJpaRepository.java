package com.campustrade.messageservice.repository;

import com.campustrade.messageservice.entity.MessageProductSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageProductSnapshotJpaRepository extends JpaRepository<MessageProductSnapshotEntity, Long> {
}
