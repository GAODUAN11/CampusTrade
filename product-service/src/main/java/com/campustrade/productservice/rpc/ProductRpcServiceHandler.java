package com.campustrade.productservice.rpc;

import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.common.rpc.RpcServiceHandler;
import com.campustrade.productservice.service.ProductService;
import org.springframework.stereotype.Component;

@Component
public class ProductRpcServiceHandler implements RpcServiceHandler {
    private final ProductService productService;

    public ProductRpcServiceHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Object handle(String methodName, Object[] arguments) {
        return switch (methodName) {
            case "listProducts" -> productService.listProducts(
                    toLong(arguments, 0),
                    (String) arguments[1],
                    toInt(arguments, 2),
                    toInt(arguments, 3)
            );
            case "getProductDetail" -> productService.getProductDetail(toLong(arguments, 0));
            case "createProduct" -> productService.createProduct((ProductCreateRequest) arguments[0]);
            case "updateProduct" -> productService.updateProduct(
                    toLong(arguments, 0),
                    (ProductUpdateRequest) arguments[1]
            );
            case "onShelf" -> productService.onShelf(toLong(arguments, 0));
            case "offShelf" -> productService.offShelf(toLong(arguments, 0));
            case "deleteProduct" -> {
                productService.deleteProduct(toLong(arguments, 0));
                yield null;
            }
            default -> throw new IllegalArgumentException("Unsupported product rpc method: " + methodName);
        };
    }

    private Long toLong(Object[] args, int index) {
        Object value = args[index];
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return (Long) value;
    }

    private int toInt(Object[] args, int index) {
        Object value = args[index];
        if (value instanceof Number number) {
            return number.intValue();
        }
        return (Integer) value;
    }
}
