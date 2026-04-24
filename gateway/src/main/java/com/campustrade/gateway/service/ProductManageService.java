package com.campustrade.gateway.service;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.common.result.ResultCode;
import com.campustrade.gateway.client.ProductServiceClient;
import com.campustrade.gateway.exception.BusinessException;
import com.campustrade.gateway.request.MyProductCreateCommand;
import org.springframework.stereotype.Service;

@Service
public class ProductManageService {
    private final ProductServiceClient productServiceClient;

    public ProductManageService(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    public ProductDTO create(Long userId, MyProductCreateCommand request) {
        ProductCreateRequest createRequest = new ProductCreateRequest();
        createRequest.setSellerId(userId);
        createRequest.setTitle(request.getTitle());
        createRequest.setDescription(request.getDescription());
        createRequest.setPrice(request.getPrice());
        createRequest.setOriginalPrice(request.getOriginalPrice());
        createRequest.setCategory(request.getCategory());
        createRequest.setLocation(request.getLocation());
        createRequest.setNegotiable(request.getNegotiable());
        createRequest.setStock(request.getStock());
        createRequest.setImageUrls(request.getImageUrls());
        createRequest.setTags(request.getTags());
        return productServiceClient.createProduct(createRequest);
    }

    public PageResponse<ProductDTO> myProducts(Long userId, int pageNo, int pageSize) {
        return productServiceClient.listProducts(userId, pageNo, pageSize);
    }

    public ProductDetailDTO myProductDetail(Long userId, Long productId) {
        ProductDetailDTO detail = productServiceClient.getProductDetail(productId);
        assertOwner(userId, detail, productId);
        return detail;
    }

    public ProductDTO update(Long userId, Long productId, ProductUpdateRequest request) {
        ProductDetailDTO detail = productServiceClient.getProductDetail(productId);
        assertOwner(userId, detail, productId);
        request.setProductId(productId);
        return productServiceClient.updateProduct(productId, request);
    }

    public ProductDTO onShelf(Long userId, Long productId) {
        ProductDetailDTO detail = productServiceClient.getProductDetail(productId);
        assertOwner(userId, detail, productId);
        return productServiceClient.onShelf(productId);
    }

    public ProductDTO offShelf(Long userId, Long productId) {
        ProductDetailDTO detail = productServiceClient.getProductDetail(productId);
        assertOwner(userId, detail, productId);
        return productServiceClient.offShelf(productId);
    }

    public void delete(Long userId, Long productId) {
        ProductDetailDTO detail = productServiceClient.getProductDetail(productId);
        assertOwner(userId, detail, productId);
        productServiceClient.deleteProduct(productId);
    }

    private void assertOwner(Long userId, ProductDetailDTO detail, Long productId) {
        Long sellerId = detail == null || detail.getProduct() == null ? null : detail.getProduct().getSellerId();
        if (sellerId == null || !sellerId.equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "No permission to operate product: " + productId);
        }
    }
}
