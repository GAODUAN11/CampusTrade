package com.campustrade.gateway.client;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.model.PageResponse;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(prefix = "campus.remote", name = "mode", havingValue = "rpc")
public class RpcFavoriteServiceClient extends BaseRemoteClient implements FavoriteServiceClient {
    private final GatewayRemoteProperties properties;

    public RpcFavoriteServiceClient(GatewayRemoteProperties properties,
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
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "checkFavorite", userId, productId);
    }

    @Override
    public PageResponse<FavoriteDTO> listMyFavorites(Long userId, int pageNo, int pageSize) {
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "listMyFavorites", userId, pageNo, pageSize);
    }

    @Override
    public FavoriteDTO addFavorite(Long userId, Long productId) {
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "addFavorite", userId, productId);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "removeFavorite", userId, productId);
    }
}
