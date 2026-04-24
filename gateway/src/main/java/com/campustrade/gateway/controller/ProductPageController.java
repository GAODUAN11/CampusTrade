package com.campustrade.gateway.controller;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.result.Result;
import com.campustrade.gateway.service.ProductQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductPageController {
    private final ProductQueryService productQueryService;

    public ProductPageController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping("/page")
    public Result<PageResponse<ProductDTO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 50);
        PageResponse<ProductDTO> page = productQueryService.page(safePageNum, safePageSize, keyword);
        return Result.success(page);
    }
}
