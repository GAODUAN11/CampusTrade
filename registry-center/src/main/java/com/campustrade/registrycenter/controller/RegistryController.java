package com.campustrade.registrycenter.controller;

import com.campustrade.common.rpc.registry.RpcServiceInstance;
import com.campustrade.common.result.Result;
import com.campustrade.registrycenter.service.RegistryStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registry")
public class RegistryController {
    private final RegistryStore registryStore;

    public RegistryController(RegistryStore registryStore) {
        this.registryStore = registryStore;
    }

    @GetMapping("/services/{serviceName}")
    public Result<List<RpcServiceInstance>> listByService(@PathVariable String serviceName) {
        return Result.success(registryStore.list(serviceName));
    }

    @GetMapping("/services")
    public Result<Map<String, List<RpcServiceInstance>>> snapshot() {
        return Result.success(registryStore.snapshot());
    }
}

