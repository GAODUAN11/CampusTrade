package com.campustrade.productservice.controller;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.common.result.Result;
import com.campustrade.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<PageResponse<ProductDTO>> listProducts(
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(productService.listProducts(sellerId, keyword, pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProductDetailDTO> getProductById(@PathVariable("id") Long productId) {
        return Result.success(productService.getProductDetail(productId));
    }

    @PostMapping
    public Result<ProductDTO> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return Result.success(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public Result<ProductDTO> updateProduct(@PathVariable("id") Long productId,
                                            @RequestBody ProductUpdateRequest request) {
        request.setProductId(productId);
        return Result.success(productService.updateProduct(productId, request));
    }

    @PutMapping("/{id}/on-shelf")
    public Result<ProductDTO> onShelf(@PathVariable("id") Long productId) {
        return Result.success(productService.onShelf(productId));
    }

    @PutMapping("/{id}/off-shelf")
    public Result<ProductDTO> offShelf(@PathVariable("id") Long productId) {
        return Result.success(productService.offShelf(productId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
        return Result.success();
    }
}
