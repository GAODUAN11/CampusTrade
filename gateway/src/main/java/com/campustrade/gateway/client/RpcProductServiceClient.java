package com.campustrade.gateway.client;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(prefix = "campus.remote", name = "mode", havingValue = "rpc")
public class RpcProductServiceClient extends BaseRemoteClient implements ProductServiceClient {
    private final GatewayRemoteProperties properties;

    public RpcProductServiceClient(GatewayRemoteProperties properties,
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

    @Override
    public ProductDetailDTO getProductDetail(Long productId) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "getProductDetail", productId);
    }

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "createProduct", request);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductUpdateRequest request) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "updateProduct", productId, request);
    }

    @Override
    public ProductDTO onShelf(Long productId) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "onShelf", productId);
    }

    @Override
    public ProductDTO offShelf(Long productId) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "offShelf", productId);
    }

    @Override
    public void deleteProduct(Long productId) {
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
