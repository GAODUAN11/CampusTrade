package com.campustrade.productservice.rpc;

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
public class ProductRpcServerBootstrap implements ApplicationRunner, DisposableBean {
    private final RpcServer rpcServer;

    public ProductRpcServerBootstrap(@Value("${campus.rpc.port:9092}") int rpcPort,
                                     @Value("${campus.rpc.protocol-version:1}") byte protocolVersion,
                                     ProductRpcServiceHandler productRpcServiceHandler) {
        this.rpcServer = new RpcServer(
                rpcPort,
                Map.of(ServiceNames.PRODUCT_SERVICE, (RpcServiceHandler) productRpcServiceHandler),
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
