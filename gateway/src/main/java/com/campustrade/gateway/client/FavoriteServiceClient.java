package com.campustrade.gateway.client;

import com.campustrade.common.dto.favorite.FavoriteDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.model.PageResponse;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import org.springframework.stereotype.Component;

@Component
public class FavoriteServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public FavoriteServiceClient(GatewayRemoteProperties properties,
                                 GatewayRpcServiceLocator serviceLocator) {
        super(serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getFavoriteService();
    }

    public Boolean checkFavorite(Long userId, Long productId) {
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "checkFavorite", userId, productId);
    }

    public PageResponse<FavoriteDTO> listMyFavorites(Long userId, int pageNo, int pageSize) {
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "listMyFavorites", userId, pageNo, pageSize);
    }

    public FavoriteDTO addFavorite(Long userId, Long productId) {
        return callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "addFavorite", userId, productId);
    }

    public void removeFavorite(Long userId, Long productId) {
        callRpc(ServiceNames.FAVORITE_SERVICE, endpoint(), "removeFavorite", userId, productId);
    }
}
