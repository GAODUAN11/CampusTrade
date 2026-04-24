package com.campustrade.gateway.controller;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("service", ServiceNames.GATEWAY);
        payload.put("status", "UP");
        payload.put("time", LocalDateTime.now().toString());
        return Result.success(payload);
    }
}
