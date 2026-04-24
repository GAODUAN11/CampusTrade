package com.campustrade.userservice.rpc;

import com.campustrade.common.rpc.RpcServiceHandler;
import com.campustrade.userservice.app.UserQueryFacade;
import com.campustrade.userservice.service.UserAuthService;
import org.springframework.stereotype.Component;

@Component
public class UserRpcServiceHandler implements RpcServiceHandler {
    private final UserQueryFacade userQueryFacade;
    private final UserAuthService userAuthService;

    public UserRpcServiceHandler(UserQueryFacade userQueryFacade, UserAuthService userAuthService) {
        this.userQueryFacade = userQueryFacade;
        this.userAuthService = userAuthService;
    }

    @Override
    public Object handle(String methodName, Object[] arguments) {
        return switch (methodName) {
            case "getUserById" -> userQueryFacade.getUserById(toLong(arguments, 0));
            case "getSellerProfile" -> userQueryFacade.getSellerProfileByUserId(toLong(arguments, 0));
            case "getUserStatus" -> userQueryFacade.getUserStatusById(toLong(arguments, 0));
            case "register" -> userAuthService.register((com.campustrade.common.request.auth.RegisterRequest) arguments[0]);
            case "login" -> userAuthService.login((com.campustrade.common.request.auth.LoginRequest) arguments[0]);
            case "logout" -> {
                userAuthService.logout((String) arguments[0]);
                yield null;
            }
            case "authenticate" -> userAuthService.authenticate((String) arguments[0]);
            default -> throw new IllegalArgumentException("Unsupported user rpc method: " + methodName);
        };
    }

    private Long toLong(Object[] args, int index) {
        Object value = args[index];
        if (value instanceof Number number) {
            return number.longValue();
        }
        return (Long) value;
    }
}
