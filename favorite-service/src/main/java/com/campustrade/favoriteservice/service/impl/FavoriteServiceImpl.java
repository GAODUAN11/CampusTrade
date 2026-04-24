package com.campustrade.favoriteservice.service.impl;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.enums.ProductStatus;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.result.ResultCode;
import com.campustrade.favoriteservice.exception.BusinessException;
import com.campustrade.favoriteservice.entity.FavoriteEntity;
import com.campustrade.favoriteservice.repository.FavoriteJpaRepository;
import com.campustrade.favoriteservice.repository.FavoriteProductSnapshotJpaRepository;
import com.campustrade.favoriteservice.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteJpaRepository favoriteRepository;
    private final FavoriteProductSnapshotJpaRepository productSnapshotRepository;

    public FavoriteServiceImpl(FavoriteJpaRepository favoriteRepository,
                               FavoriteProductSnapshotJpaRepository productSnapshotRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productSnapshotRepository = productSnapshotRepository;
    }

    @Override
    public FavoriteDTO addFavorite(Long userId, Long productId) {
        validateId(userId, "userId");
        validateId(productId, "productId");

        return favoriteRepository.findByUserIdAndProductId(userId, productId)
                .map(this::toFavoriteDTO)
                .orElseGet(() -> {
                    FavoriteEntity entity = new FavoriteEntity();
                    entity.setUserId(userId);
                    entity.setProductId(productId);
                    FavoriteEntity saved = favoriteRepository.save(entity);
                    return toFavoriteDTO(saved);
                });
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        validateId(userId, "userId");
        validateId(productId, "productId");
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(Long userId, Long productId) {
        validateId(userId, "userId");
        validateId(productId, "productId");
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FavoriteDTO> listMyFavorites(Long userId, int pageNo, int pageSize) {
        validateId(userId, "userId");
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);

        PageRequest pageRequest = PageRequest.of(
                safePageNo - 1,
                safePageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<FavoriteEntity> page = favoriteRepository.findByUserId(userId, pageRequest);
        List<FavoriteDTO> records = page.getContent().stream().map(this::toFavoriteDTO).toList();
        return PageResponse.of(page.getTotalElements(), safePageNo, safePageSize, records);
    }

    private FavoriteDTO toFavoriteDTO(FavoriteEntity record) {
        ProductSnapshot snapshot = findSnapshot(record.getProductId());
        FavoriteDTO dto = new FavoriteDTO();
        dto.setFavoriteId(record.getFavoriteId());
        dto.setUserId(record.getUserId());
        dto.setProductId(record.getProductId());
        dto.setProductTitle(snapshot.title);
        dto.setProductCoverImageUrl(snapshot.coverImageUrl);
        dto.setProductPrice(snapshot.price);
        dto.setProductStatus(snapshot.status);
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, fieldName + " is invalid.");
        }
    }

    private ProductSnapshot findSnapshot(Long productId) {
        return productSnapshotRepository.findById(productId)
                .map(entity -> new ProductSnapshot(
                        entity.getTitle(),
                        entity.getCoverImageUrl(),
                        entity.getPrice(),
                        entity.getStatus()
                ))
                .orElseGet(ProductSnapshot::defaultSnapshot);
    }

    private static class ProductSnapshot {
        private final String title;
        private final String coverImageUrl;
        private final BigDecimal price;
        private final ProductStatus status;

        private ProductSnapshot(String title, String coverImageUrl, BigDecimal price, ProductStatus status) {
            this.title = title;
            this.coverImageUrl = coverImageUrl;
            this.price = price;
            this.status = status;
        }

        private static ProductSnapshot defaultSnapshot() {
            return new ProductSnapshot(
                    "Unknown Product",
                    "https://example.com/product/default.jpg",
                    BigDecimal.ZERO,
                    ProductStatus.OFF_SHELF
            );
        }
    }
}
