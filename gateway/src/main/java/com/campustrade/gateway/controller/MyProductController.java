package com.campustrade.gateway.controller;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.common.result.Result;
import com.campustrade.gateway.request.MyProductCreateCommand;
import com.campustrade.gateway.security.CurrentUserId;
import com.campustrade.gateway.service.ProductManageService;
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
@RequestMapping("/api/v1/me/products")
public class MyProductController {
    private final ProductManageService productManageService;

    public MyProductController(ProductManageService productManageService) {
        this.productManageService = productManageService;
    }

    @PostMapping
    public Result<ProductDTO> create(
            @CurrentUserId Long userId,
            @Valid @RequestBody MyProductCreateCommand request
    ) {
        return Result.success(productManageService.create(userId, request));
    }

    @GetMapping("/page")
    public Result<PageResponse<ProductDTO>> myProducts(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);
        return Result.success(productManageService.myProducts(userId, safePageNo, safePageSize));
    }

    @GetMapping("/{productId}")
    public Result<ProductDetailDTO> myProductDetail(
            @CurrentUserId Long userId,
            @PathVariable Long productId
    ) {
        return Result.success(productManageService.myProductDetail(userId, productId));
    }

    @PutMapping("/{productId}")
    public Result<ProductDTO> update(
            @CurrentUserId Long userId,
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request
    ) {
        return Result.success(productManageService.update(userId, productId, request));
    }

    @PutMapping("/{productId}/on-shelf")
    public Result<ProductDTO> onShelf(@CurrentUserId Long userId, @PathVariable Long productId) {
        return Result.success(productManageService.onShelf(userId, productId));
    }

    @PutMapping("/{productId}/off-shelf")
    public Result<ProductDTO> offShelf(@CurrentUserId Long userId, @PathVariable Long productId) {
        return Result.success(productManageService.offShelf(userId, productId));
    }

    @DeleteMapping("/{productId}")
    public Result<Void> delete(@CurrentUserId Long userId, @PathVariable Long productId) {
        productManageService.delete(userId, productId);
        return Result.success();
    }
}
