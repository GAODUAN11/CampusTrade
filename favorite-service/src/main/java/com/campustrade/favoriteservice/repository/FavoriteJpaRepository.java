package com.campustrade.favoriteservice.repository;

import com.campustrade.favoriteservice.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteJpaRepository extends JpaRepository<FavoriteEntity, Long> {
    Optional<FavoriteEntity> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    Page<FavoriteEntity> findByUserId(Long userId, Pageable pageable);
}
