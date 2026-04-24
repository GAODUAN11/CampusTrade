package com.campustrade.userservice.repository;

import com.campustrade.userservice.entity.SellerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileJpaRepository extends JpaRepository<SellerProfileEntity, Long> {
}
