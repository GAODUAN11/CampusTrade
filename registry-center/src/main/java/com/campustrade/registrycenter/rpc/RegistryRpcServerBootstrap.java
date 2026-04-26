package com.campustrade.registrycenter.rpc;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.rpc.RpcServer;
import com.campustrade.common.rpc.RpcServiceHandler;
import com.campustrade.registrycenter.config.RegistryCenterProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RegistryRpcServerBootstrap implements ApplicationRunner, DisposableBean {
    private final RpcServer rpcServer;

    public RegistryRpcServerBootstrap(RegistryCenterProperties properties,
                                      RegistryRpcServiceHandler registryRpcServiceHandler) {
        this.rpcServer = new RpcServer(
                properties.getRpcPort(),
                Map.of(ServiceNames.REGISTRY_CENTER, (RpcServiceHandler) registryRpcServiceHandler),
                Runtime.getRuntime().availableProcessors(),
                properties.getProtocolVersion()
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

