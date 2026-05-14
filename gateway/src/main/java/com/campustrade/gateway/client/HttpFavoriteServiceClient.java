package com.campustrade.gateway.client;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@ConditionalOnProperty(
        prefix = "campus.remote",
        name = "mode",
        havingValue = "http",
        matchIfMissing = true
)
public class HttpFavoriteServiceClient extends BaseRemoteClient implements FavoriteServiceClient {
    private final GatewayRemoteProperties properties;

    public HttpFavoriteServiceClient(GatewayRemoteProperties properties,
                                     RestTemplate restTemplate,
                                     ObjectMapper objectMapper,
                                     GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getFavoriteService();
    }

    @Override
    public Boolean checkFavorite(Long userId, Long productId) {
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

    @Override
    public PageResponse<FavoriteDTO> listMyFavorites(Long userId, int pageNo, int pageSize) {
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

    @Override
    public FavoriteDTO addFavorite(Long userId, Long productId) {
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

    @Override
    public void removeFavorite(Long userId, Long productId) {
        callHttpNoContent(
                HttpMethod.DELETE,
                endpoint(),
                "/api/favorites",
                null,
                Map.of(),
                Map.of("userId", userId, "productId", productId)
        );
    }
}
