package com.campustrade.registrycenter.controller;

import com.campustrade.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class RegistryHealthController {

    @GetMapping
    public Result<Map<String, Object>> health() {
        return Result.success(Map.of(
                "service", "registry-center",
                "status", "UP",
                "timestamp", System.currentTimeMillis()
        ));
    }
}

