package com.campustrade.userservice.rpc;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.rpc.RpcServer;
import com.campustrade.common.rpc.RpcServiceHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserRpcServerBootstrap implements ApplicationRunner, DisposableBean {
    private final RpcServer rpcServer;

    public UserRpcServerBootstrap(@Value("${campus.rpc.port:9091}") int rpcPort,
                                  @Value("${campus.rpc.protocol-version:1}") byte protocolVersion,
                                  UserRpcServiceHandler userRpcServiceHandler) {
        this.rpcServer = new RpcServer(
                rpcPort,
                Map.of(ServiceNames.USER_SERVICE, (RpcServiceHandler) userRpcServiceHandler),
                Runtime.getRuntime().availableProcessors(),
                protocolVersion
        );
    }

    @Override
    public void run(ApplicationArguments args) {
        rpcServer.start();
    }

    @Override
    public void destroy() {
        rpcServer.stop();
    }
}
