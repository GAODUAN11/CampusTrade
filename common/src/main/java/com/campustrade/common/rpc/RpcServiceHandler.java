package com.campustrade.common.rpc;

@FunctionalInterface
public interface RpcServiceHandler {
    Object handle(String methodName, Object[] arguments) throws Exception;
}
