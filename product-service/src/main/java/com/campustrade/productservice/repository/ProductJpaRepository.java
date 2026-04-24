package com.campustrade.productservice.repository;

import com.campustrade.common.enums.ProductStatus;
import com.campustrade.productservice.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findByStatusNot(ProductStatus status, Pageable pageable);

    Page<ProductEntity> findBySellerIdAndStatusNot(Long sellerId, ProductStatus status, Pageable pageable);

    @Query("""
            SELECT p
            FROM ProductEntity p
            WHERE p.status <> :excludedStatus
              AND (:sellerId IS NULL OR p.sellerId = :sellerId)
              AND (:keyword IS NULL OR :keyword = ''
                   OR lower(p.title) LIKE lower(concat('%', :keyword, '%'))
                   OR lower(p.summary) LIKE lower(concat('%', :keyword, '%'))
                   OR lower(p.category) LIKE lower(concat('%', :keyword, '%'))
                   OR lower(p.location) LIKE lower(concat('%', :keyword, '%')))
            """)
    Page<ProductEntity> search(
            @Param("sellerId") Long sellerId,
            @Param("keyword") String keyword,
            @Param("excludedStatus") ProductStatus excludedStatus,
            Pageable pageable
    );
}
