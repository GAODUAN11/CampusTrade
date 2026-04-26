package com.campustrade.gateway.client;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.model.PageResponse;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class FavoriteServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public FavoriteServiceClient(GatewayRemoteProperties properties,
                                 RestTemplate restTemplate,
                                 ObjectMapper objectMapper,
                                 GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getFavoriteService();
    }

    public Boolean checkFavorite(Long userId, Long productId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/favorites/check",
                    null,
                    Map.of(),
                    Map.of("userId", userId, "productId", productId),
                    Boolean.class
            );
        }
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "checkFavorite", userId, productId);
    }

    public PageResponse<FavoriteDTO> listMyFavorites(Long userId, int pageNo, int pageSize) {
        if (!useRpc()) {
            return callHttpForType(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/favorites/my",
                    null,
                    Map.of(),
                    Map.of("userId", userId, "pageNo", pageNo, "pageSize", pageSize),
                    new TypeReference<PageResponse<FavoriteDTO>>() {
                    }
            );
        }
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "listMyFavorites", userId, pageNo, pageSize);
    }

    public FavoriteDTO addFavorite(Long userId, Long productId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.POST,
                    endpoint(),
                    "/api/favorites",
                    null,
                    Map.of(),
                    Map.of("userId", userId, "productId", productId),
                    FavoriteDTO.class
            );
        }
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "addFavorite", userId, productId);
    }

    public void removeFavorite(Long userId, Long productId) {
        if (!useRpc()) {
            callHttpNoContent(
                    HttpMethod.DELETE,
                    endpoint(),
                    "/api/favorites",
                    null,
                    Map.of(),
                    Map.of("userId", userId, "productId", productId)
            );
            return;
        }
        callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "removeFavorite", userId, productId);
    }
}
