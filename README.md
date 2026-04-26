# CampusTrade

CampusTrade 是一个校园二手交易平台，当前采用分布式架构：

- 本地客户端：`gateway`（主站与聚合层）
- 云服务器 A：`user-service`、`product-service`
- 云服务器 B：`favorite-service`、`message-service`
- 注册中心：`registry-center`

## 1. 模块清单

- `common`：DTO/Request/Result/常量/自研 RPC 基础代码
- `registry-center`：独立注册中心（TCP-RPC 注册/心跳/发现）
- `gateway`：Web 入口、聚合逻辑、登录态校验
- `user-service`：用户与认证
- `product-service`：商品
- `favorite-service`：收藏
- `message-service`：会话与消息

## 2. 通信模式

### 无 RPC 模式（本地联调优先）

- `gateway` 通过 HTTP 调用各服务
- 配置：`campus.remote.mode=http`

### RPC 模式（实验要求）

- 通讯：`TCP Socket` + Java 序列化协议帧（项目内实现）
- 服务发现：`registry-center`
- 提供者启动后自动注册 + 心跳；网关从注册中心发现并调用
- 配置：`campus.remote.mode=rpc`

## 3. 本地快速启动（无 RPC）

1. 启动 MySQL，并执行初始化 SQL：

```sql
source sql/init-mysql-databases.sql;
```

2. 分别启动服务（IDEA 直接运行）：

- `UserServiceApplication`
- `ProductServiceApplication`
- `FavoriteServiceApplication`
- `MessageServiceApplication`
- `GatewayApplication`

3. 确认 `gateway/src/main/resources/application.yml`：

- `campus.remote.mode=http`
- 四个 `base-url` 指向本地 8081/8082/8083/8084

## 4. RPC 模式启动顺序

1. 启动 `RegistryCenterApplication`
2. 启动四个业务服务（建议启用 profile `rpc`）
3. 启动 `GatewayApplication`（profile `rpc` 或 `CAMPUS_REMOTE_MODE=rpc`）

说明：

- 服务端 `application-rpc.yml` 已打开 `campus.rpc.registry.enabled=true`
- gateway `application-rpc.yml` 已打开 `campus.remote.mode=rpc`

## 5. 部署脚本

详见：[deploy/README.md](deploy/README.md)

