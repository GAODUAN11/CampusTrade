package com.campustrade.gateway.service;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.gateway.client.ProductServiceClient;
import org.springframework.stereotype.Service;

@Service
public class ProductQueryService {
    private final ProductServiceClient productServiceClient;

    public ProductQueryService(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    public PageResponse<ProductDTO> page(int pageNum, int pageSize, String keyword) {
        return productServiceClient.listProducts(null, keyword, pageNum, pageSize);
    }
}
