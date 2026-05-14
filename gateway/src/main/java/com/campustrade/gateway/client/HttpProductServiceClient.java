package com.campustrade.gateway.client;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(
        prefix = "campus.remote",
        name = "mode",
        havingValue = "http",
        matchIfMissing = true
)
public class HttpProductServiceClient extends BaseRemoteClient implements ProductServiceClient {
    private final GatewayRemoteProperties properties;

    public HttpProductServiceClient(GatewayRemoteProperties properties,
                                    RestTemplate restTemplate,
                                    ObjectMapper objectMapper,
                                    GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getProductService();
    }

    @Override
    public PageResponse<ProductDTO> listProducts(int pageNo, int pageSize) {
        return listProducts(null, null, pageNo, pageSize);
    }

    @Override
    public PageResponse<ProductDTO> listProducts(Long sellerId, int pageNo, int pageSize) {
        return listProducts(sellerId, null, pageNo, pageSize);
    }

    @Override
    public PageResponse<ProductDTO> listProducts(Long sellerId, String keyword, int pageNo, int pageSize) {
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

    @Override
    public ProductDetailDTO getProductDetail(Long productId) {
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

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        return callHttpForObject(
                HttpMethod.POST,
                endpoint(),
                "/api/products",
                request,
                ProductDTO.class
        );
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductUpdateRequest request) {
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

    @Override
    public ProductDTO onShelf(Long productId) {
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

    @Override
    public ProductDTO offShelf(Long productId) {
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

    @Override
    public void deleteProduct(Long productId) {
        callHttpNoContent(
                HttpMethod.DELETE,
                endpoint(),
                "/api/products/{id}",
                null,
                Map.of("id", productId),
                Map.of()
        );
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String normalized = keyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
