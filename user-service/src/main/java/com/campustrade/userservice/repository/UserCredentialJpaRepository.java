package com.campustrade.userservice.repository;

import com.campustrade.userservice.entity.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialJpaRepository extends JpaRepository<UserCredentialEntity, Long> {
}
