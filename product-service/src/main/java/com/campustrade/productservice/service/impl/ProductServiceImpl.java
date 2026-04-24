package com.campustrade.productservice.service.impl;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.dto.user.SellerProfileDTO;
import com.campustrade.common.enums.ProductStatus;
import com.campustrade.common.enums.UserStatus;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.common.result.ResultCode;
import com.campustrade.productservice.exception.BusinessException;
import com.campustrade.productservice.entity.ProductEntity;
import com.campustrade.productservice.repository.ProductJpaRepository;
import com.campustrade.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductJpaRepository productRepository;

    public ProductServiceImpl(ProductJpaRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> listProducts(Long sellerId, String keyword, int pageNo, int pageSize) {
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);
        String normalizedKeyword = normalizeKeyword(keyword);

        PageRequest pageRequest = PageRequest.of(
                safePageNo - 1,
                safePageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Long normalizedSellerId = (sellerId != null && sellerId > 0) ? sellerId : null;
        Page<ProductEntity> page = productRepository.search(
                normalizedSellerId,
                normalizedKeyword,
                ProductStatus.DELETED,
                pageRequest
        );
        List<ProductDTO> records = page.getContent().stream().map(this::toProductDTO).toList();
        return PageResponse.of(page.getTotalElements(), safePageNo, safePageSize, records);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetail(Long productId) {
        ProductEntity entity = findByIdOrThrow(productId);

        ProductDetailDTO detail = new ProductDetailDTO();
        detail.setProduct(toProductDTO(entity));
        detail.setSeller(buildSellerProfile(entity.getSellerId()));
        detail.setDescription(entity.getDescription());
        detail.setImageUrls(copyList(entity.getImageUrls()));
        detail.setTags(copyList(entity.getTags()));
        detail.setFavoritedByCurrentUser(Boolean.FALSE);
        return detail;
    }

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        validateCreateRequest(request);
        LocalDateTime now = LocalDateTime.now();

        ProductEntity entity = new ProductEntity();
        entity.setSellerId(request.getSellerId());
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setSummary(buildSummary(request.getDescription()));
        entity.setPrice(request.getPrice());
        entity.setOriginalPrice(defaultOriginalPrice(request.getOriginalPrice(), request.getPrice()));
        entity.setCategory(request.getCategory());
        entity.setLocation(request.getLocation());
        entity.setNegotiable(Boolean.TRUE.equals(request.getNegotiable()));
        entity.setStock(defaultStock(request.getStock()));
        entity.setImageUrls(copyList(request.getImageUrls()));
        entity.setTags(copyList(request.getTags()));
        entity.setCoverImageUrl(resolveCoverImage(request.getImageUrls()));
        entity.setViewCount(0);
        entity.setFavoriteCount(0);
        entity.setStatus(ProductStatus.ON_SALE);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        ProductEntity saved = productRepository.save(entity);
        return toProductDTO(saved);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductUpdateRequest request) {
        if (!Objects.equals(productId, request.getProductId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Path id does not match request productId.");
        }

        ProductEntity existing = findByIdOrThrow(productId);
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
            existing.setSummary(buildSummary(request.getDescription()));
        }
        if (request.getPrice() != null) {
            existing.setPrice(request.getPrice());
        }
        if (request.getOriginalPrice() != null) {
            existing.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getCategory() != null) {
            existing.setCategory(request.getCategory());
        }
        if (request.getLocation() != null) {
            existing.setLocation(request.getLocation());
        }
        if (request.getNegotiable() != null) {
            existing.setNegotiable(request.getNegotiable());
        }
        if (request.getStock() != null) {
            if (request.getStock() <= 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "stock must be greater than 0.");
            }
            existing.setStock(request.getStock());
        }
        if (request.getImageUrls() != null) {
            existing.setImageUrls(copyList(request.getImageUrls()));
            existing.setCoverImageUrl(resolveCoverImage(request.getImageUrls()));
        }
        if (request.getTags() != null) {
            existing.setTags(copyList(request.getTags()));
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        ProductEntity updated = productRepository.save(existing);
        return toProductDTO(updated);
    }

    @Override
    public ProductDTO onShelf(Long productId) {
        return updateStatus(productId, ProductStatus.ON_SALE);
    }

    @Override
    public ProductDTO offShelf(Long productId) {
        return updateStatus(productId, ProductStatus.OFF_SHELF);
    }

    @Override
    public void deleteProduct(Long productId) {
        ProductEntity existing = findByIdOrThrow(productId);
        existing.setStatus(ProductStatus.DELETED);
        existing.setUpdatedAt(LocalDateTime.now());
        productRepository.save(existing);
    }

    private ProductDTO updateStatus(Long productId, ProductStatus status) {
        ProductEntity existing = findByIdOrThrow(productId);
        existing.setStatus(status);
        existing.setUpdatedAt(LocalDateTime.now());
        return toProductDTO(productRepository.save(existing));
    }

    private ProductEntity findByIdOrThrow(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Invalid product id.");
        }
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Product not found: " + productId));
        if (entity.getStatus() == ProductStatus.DELETED) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Product not found: " + productId);
        }
        return entity;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String normalized = keyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void validateCreateRequest(ProductCreateRequest request) {
        if (request.getSellerId() == null || request.getSellerId() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "sellerId is invalid.");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "price must be greater than 0.");
        }
        if (request.getStock() != null && request.getStock() <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "stock must be greater than 0.");
        }
    }

    private ProductDTO toProductDTO(ProductEntity entity) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(entity.getProductId());
        dto.setSellerId(entity.getSellerId());
        dto.setTitle(entity.getTitle());
        dto.setSummary(entity.getSummary());
        dto.setPrice(entity.getPrice());
        dto.setOriginalPrice(entity.getOriginalPrice());
        dto.setCategory(entity.getCategory());
        dto.setLocation(entity.getLocation());
        dto.setCoverImageUrl(entity.getCoverImageUrl());
        dto.setViewCount(entity.getViewCount());
        dto.setFavoriteCount(entity.getFavoriteCount());
        dto.setNegotiable(entity.getNegotiable());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private SellerProfileDTO buildSellerProfile(Long sellerId) {
        SellerProfileDTO seller = new SellerProfileDTO();
        seller.setUserId(sellerId);
        seller.setNickname("Seller-" + sellerId);
        seller.setAvatarUrl("https://example.com/avatar/seller-" + sellerId + ".png");
        seller.setSchool("Campus University");
        seller.setCampus("Main Campus");
        seller.setCreditScore(90);
        seller.setProductCount(10);
        seller.setSoldCount(24);
        seller.setOnline(Boolean.FALSE);
        seller.setStatus(UserStatus.ACTIVE);
        seller.setJoinedAt(LocalDateTime.now().minusMonths(10));
        return seller;
    }

    private String buildSummary(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "";
        }
        String normalized = description.trim();
        int max = 80;
        return normalized.length() <= max ? normalized : normalized.substring(0, max);
    }

    private BigDecimal defaultOriginalPrice(BigDecimal originalPrice, BigDecimal price) {
        if (originalPrice != null) {
            return originalPrice;
        }
        return price;
    }

    private Integer defaultStock(Integer stock) {
        if (stock == null) {
            return 1;
        }
        return stock;
    }

    private List<String> copyList(List<String> source) {
        if (source == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(source);
    }

    private String resolveCoverImage(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return "https://example.com/product/default.jpg";
        }
        String first = imageUrls.get(0);
        return (first == null || first.trim().isEmpty())
                ? "https://example.com/product/default.jpg"
                : first;
    }
}
