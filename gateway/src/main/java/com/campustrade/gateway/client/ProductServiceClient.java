package com.campustrade.gateway.client;

import com.campustrade.common.dto.product.ProductDTO;
import com.campustrade.common.dto.product.ProductDetailDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.model.PageResponse;
import com.campustrade.common.request.product.ProductCreateRequest;
import com.campustrade.common.request.product.ProductUpdateRequest;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public ProductServiceClient(GatewayRemoteProperties properties,
                                GatewayRpcServiceLocator serviceLocator) {
        super(serviceLocator);
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
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "getProductDetail", productId);
    }

    public ProductDTO createProduct(ProductCreateRequest request) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "createProduct", request);
    }

    public ProductDTO updateProduct(Long productId, ProductUpdateRequest request) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "updateProduct", productId, request);
    }

    public ProductDTO onShelf(Long productId) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "onShelf", productId);
    }

    public ProductDTO offShelf(Long productId) {
        return callRpc(ServiceNames.PRODUCT_SERVICE, endpoint(), "offShelf", productId);
    }

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
