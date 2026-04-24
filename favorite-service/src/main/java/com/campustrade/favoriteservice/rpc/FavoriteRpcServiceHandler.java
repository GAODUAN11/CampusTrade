package com.campustrade.favoriteservice.rpc;

import com.campustrade.common.rpc.RpcServiceHandler;
import com.campustrade.favoriteservice.service.FavoriteService;
import org.springframework.stereotype.Component;

@Component
public class FavoriteRpcServiceHandler implements RpcServiceHandler {
    private final FavoriteService favoriteService;

    public FavoriteRpcServiceHandler(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Override
    public Object handle(String methodName, Object[] arguments) {
        return switch (methodName) {
            case "addFavorite" -> favoriteService.addFavorite(toLong(arguments, 0), toLong(arguments, 1));
            case "removeFavorite" -> {
                favoriteService.removeFavorite(toLong(arguments, 0), toLong(arguments, 1));
                yield null;
            }
            case "checkFavorite" -> favoriteService.isFavorited(toLong(arguments, 0), toLong(arguments, 1));
            case "listMyFavorites" -> favoriteService.listMyFavorites(
                    toLong(arguments, 0),
                    toInt(arguments, 1),
                    toInt(arguments, 2)
            );
            default -> throw new IllegalArgumentException("Unsupported favorite rpc method: " + methodName);
        };
    }

    private Long toLong(Object[] args, int index) {
        Object value = args[index];
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
