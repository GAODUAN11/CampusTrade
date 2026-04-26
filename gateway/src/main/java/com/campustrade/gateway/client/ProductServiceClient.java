package com.campustrade.gateway.client;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public ProductServiceClient(GatewayRemoteProperties properties,
                                RestTemplate restTemplate,
                                ObjectMapper objectMapper,
                                GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getProductService();
    }

    public PageResponse<ProductDTO> listProducts(int pageNo, int pageSize) {
        return listProducts(null, null, pageNo, pageSize);
    }

    public PageResponse<ProductDTO> listProducts(Long sellerId, int pageNo, int pageSize) {
        return listProducts(sellerId, null, pageNo, pageSize);
    }

    public PageResponse<ProductDTO> listProducts(Long sellerId, String keyword, int pageNo, int pageSize) {
        if (!useRpc()) {
            Map<String, Object> query = new HashMap<>();
            query.put("pageNo", pageNo);
            query.put("pageSize", pageSize);
            if (sellerId != null) {
                query.put("sellerId", sellerId);
            }
            String normalizedKeyword = normalizeKeyword(keyword);
            if (normalizedKeyword != null) {
                query.put("keyword", normalizedKeyword);
            }
            return callHttpForType(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/products",
                    null,
                    Map.of(),
                    query,
                    new TypeReference<PageResponse<ProductDTO>>() {
                    }
            );
        }
        return callRpc(
                ServiceNames.PRODUCT_SERVICE,
                endpoint(),
                "listProducts",
                sellerId,
                normalizeKeyword(keyword),
                pageNo,
                pageSize
        );
    }

    public ProductDetailDTO getProductDetail(Long productId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/products/{id}",
                    null,
                    Map.of("id", productId),
                    Map.of(),
                    ProductDetailDTO.class
            );
        }
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "getProductDetail", productId);
    }

    public ProductDTO createProduct(ProductCreateRequest request) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.POST,
                    endpoint(),
                    "/api/products",
                    request,
                    ProductDTO.class
            );
        }
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "createProduct", request);
    }

    public ProductDTO updateProduct(Long productId, ProductUpdateRequest request) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.PUT,
                    endpoint(),
                    "/api/products/{id}",
                    request,
                    Map.of("id", productId),
                    Map.of(),
                    ProductDTO.class
            );
        }
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "updateProduct", productId, request);
    }

    public ProductDTO onShelf(Long productId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.PUT,
                    endpoint(),
                    "/api/products/{id}/on-shelf",
                    null,
                    Map.of("id", productId),
                    Map.of(),
                    ProductDTO.class
            );
        }
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "onShelf", productId);
    }

    public ProductDTO offShelf(Long productId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.PUT,
                    endpoint(),
                    "/api/products/{id}/off-shelf",
                    null,
                    Map.of("id", productId),
                    Map.of(),
                    ProductDTO.class
            );
        }
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "offShelf", productId);
    }

    public void deleteProduct(Long productId) {
        if (!useRpc()) {
            callHttpNoContent(
                    HttpMethod.DELETE,
                    endpoint(),
                    "/api/products/{id}",
                    null,
                    Map.of("id", productId),
                    Map.of()
            );
            return;
        }
        callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "deleteProduct", productId);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String normalized = keyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
