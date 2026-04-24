package com.campustrade.userservice.repository;

import com.campustrade.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByPhone(String phone);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmailIgnoreCase(String email);

    @Query("select coalesce(max(u.userId), 10000) from UserEntity u")
    Long findMaxUserId();
}
