package com.campustrade.favoriteservice.repository;

import com.campustrade.favoriteservice.entity.FavoriteProductSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteProductSnapshotJpaRepository extends JpaRepository<FavoriteProductSnapshotEntity, Long> {
}
