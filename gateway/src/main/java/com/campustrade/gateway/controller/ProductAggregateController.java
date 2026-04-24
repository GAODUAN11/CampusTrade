package com.campustrade.gateway.controller;

import com.campustrade.common.result.Result;
import com.campustrade.gateway.service.ProductAggregateService;
import com.campustrade.gateway.vo.ProductDetailPageVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductAggregateController {
    private final ProductAggregateService productAggregateService;

    public ProductAggregateController(ProductAggregateService productAggregateService) {
        this.productAggregateService = productAggregateService;
    }

    @GetMapping("/{productId}/detail")
    public Result<ProductDetailPageVO> detail(
            @PathVariable Long productId,
            @RequestParam(required = false) Long userId
    ) {
        ProductDetailPageVO detail = productAggregateService.aggregateProductDetail(productId, userId);
        return Result.success(detail);
    }

    @GetMapping("/{productId}/aggregate")
    public Result<ProductDetailPageVO> aggregate(
            @PathVariable Long productId,
            @RequestParam(required = false) Long userId
    ) {
        ProductDetailPageVO detail = productAggregateService.aggregateProductDetail(productId, userId);
        return Result.success(detail);
    }
}
