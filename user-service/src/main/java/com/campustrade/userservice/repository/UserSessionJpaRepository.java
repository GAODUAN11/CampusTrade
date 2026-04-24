package com.campustrade.userservice.repository;

import com.campustrade.userservice.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface UserSessionJpaRepository extends JpaRepository<UserSessionEntity, String> {
    @Transactional
    long deleteByExpiresAtBefore(LocalDateTime now);
}
