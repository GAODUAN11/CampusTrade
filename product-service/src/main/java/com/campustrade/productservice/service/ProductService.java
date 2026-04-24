package com.campustrade.productservice.service;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;

public interface ProductService {
    PageResponse<ProductDTO> listProducts(Long sellerId, String keyword, int pageNo, int pageSize);

    ProductDetailDTO getProductDetail(Long productId);

    ProductDTO createProduct(ProductCreateRequest request);

    ProductDTO updateProduct(Long productId, ProductUpdateRequest request);

    ProductDTO onShelf(Long productId);

    ProductDTO offShelf(Long productId);

    void deleteProduct(Long productId);
}
