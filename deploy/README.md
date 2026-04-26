# CampusTrade 部署与演示说明

本文档对应实验架构：`2个业务服务器 + 1个注册中心 + 1个客户端(gateway)`。

## 1. 节点职责

- 注册中心节点：`registry-center`（HTTP 8070 / RPC 9090）
- 云服务器 A：`user-service`（8081/9091）、`product-service`（8082/9092）
- 云服务器 B：`favorite-service`（8083/9093）、`message-service`（8084/9094）
- 客户端（本地 Win11）：`gateway`（8080）

## 2. 两种演示模式

### 模式 A：无 RPC（HTTP 直连）

- `gateway` 使用 `campus.remote.mode=http`
- `gateway` 通过 `base-url` 直接请求四个服务的 REST 接口
- 不依赖注册中心

### 模式 B：自研 RPC（TCP Socket + 注册中心）

- `gateway` 使用 `campus.remote.mode=rpc`
- 四个业务服务启动后自动向 `registry-center` 注册并持续心跳
- `gateway` 从注册中心发现服务，RPC 调用失败时按实例列表重试

## 3. 服务器端脚本

### 3.1 注册中心节点

```bash
chmod +x deploy/common/service-functions.sh deploy/registry/*.sh
cp deploy/env/registry.env.example deploy/env/registry.env

./deploy/registry/prepare.sh
./deploy/registry/start.sh
./deploy/registry/status.sh
```

日志与 PID：

- `logs/registry/registry-center.log`
- `run/registry/registry-center.pid`

### 3.2 云服务器 A

```bash
chmod +x deploy/common/service-functions.sh deploy/server-a/*.sh
cp deploy/env/server-a.env.example deploy/env/server-a.env

./deploy/server-a/prepare.sh
./deploy/server-a/start.sh
./deploy/server-a/status.sh
```

### 3.3 云服务器 B

```bash
chmod +x deploy/common/service-functions.sh deploy/server-b/*.sh
cp deploy/env/server-b.env.example deploy/env/server-b.env

./deploy/server-b/prepare.sh
./deploy/server-b/start.sh
./deploy/server-b/status.sh
```

## 4. RPC 模式下 env 关键变量

在 `deploy/env/server-a.env` 和 `deploy/env/server-b.env` 中：

```bash
SPRING_PROFILES_ACTIVE=rpc
CAMPUS_REGISTRY_ENABLED=true
CAMPUS_REGISTRY_HOST=<REGISTRY_SERVER_IP>
CAMPUS_REGISTRY_RPC_PORT=9090
CAMPUS_RPC_ADVERTISE_HOST=<CURRENT_SERVER_IP>
```

## 5. 客户端（gateway）配置

### 无 RPC 演示

- `campus.remote.mode=http`
- 配置：
  - `campus.remote.user-service.base-url`
  - `campus.remote.product-service.base-url`
  - `campus.remote.favorite-service.base-url`
  - `campus.remote.message-service.base-url`

### RPC 演示

- `campus.remote.mode=rpc` 或启用 profile `rpc`
- 配置：
  - `CAMPUS_REGISTRY_HOST=<REGISTRY_SERVER_IP>`
  - `CAMPUS_REGISTRY_RPC_PORT=9090`

## 6. 故障演示建议

1. 先在 RPC 模式下全部启动。
2. 访问注册中心：`GET http://<registry-host>:8070/api/registry/services`，确认已注册。
3. 停掉某个服务实例，等待心跳 TTL（默认 15 秒）后再次查看注册信息。
4. 观察 gateway 聚合接口：  
   - 依赖该服务的接口会失败或降级  
   - 不依赖该服务的接口仍可正常访问

