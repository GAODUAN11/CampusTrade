# CampusTrade Server Deployment Scripts

This folder provides Linux shell scripts for your current architecture:

- Server A: `user-service` (HTTP 8081 / RPC 9091), `product-service` (HTTP 8082 / RPC 9092)
- Server B: `favorite-service` (HTTP 8083 / RPC 9093), `message-service` (HTTP 8084 / RPC 9094)

## 1. Prerequisites

- Linux with `bash`
- Java 17
- Maven 3.9+
- MySQL 8+
- Open ports:
  - Server A: `8081`, `8082`, `9091`, `9092`
  - Server B: `8083`, `8084`, `9093`, `9094`

## 2. Configure Environment Files

On each server, create env files from templates:

```bash
cp deploy/env/server-a.env.example deploy/env/server-a.env
cp deploy/env/server-b.env.example deploy/env/server-b.env
```

Edit values:

- `CAMPUS_MYSQL_HOST`
- `CAMPUS_MYSQL_PORT`
- `CAMPUS_MYSQL_USERNAME`
- `CAMPUS_MYSQL_PASSWORD`
- `JAVA_OPTS`

Important:

- Keep env files in `KEY=VALUE` format (no `export` prefix) for systemd compatibility.
- `java -version` must be 17 or above.

## 3. Server A Commands

```bash
chmod +x deploy/common/service-functions.sh deploy/server-a/*.sh

# first-time prepare (build runnable jars)
./deploy/server-a/prepare.sh

# start services
./deploy/server-a/start.sh

# check status
./deploy/server-a/status.sh

# stop services
./deploy/server-a/stop.sh
```

Logs and pid files:

- Logs: `logs/server-a/*.log`
- PID: `run/server-a/*.pid`

## 4. Server B Commands

```bash
chmod +x deploy/common/service-functions.sh deploy/server-b/*.sh

# first-time prepare (build runnable jars)
./deploy/server-b/prepare.sh

# start services
./deploy/server-b/start.sh

# check status
./deploy/server-b/status.sh

# stop services
./deploy/server-b/stop.sh
```

Logs and pid files:

- Logs: `logs/server-b/*.log`
- PID: `run/server-b/*.pid`

## 5. Gateway Remote Service Configuration

After server deployment, update gateway RPC endpoints:

- `campus.remote.user-service.host` -> `<SERVER_A_IP>`
- `campus.remote.user-service.rpc-port` -> `9091`
- `campus.remote.product-service.host` -> `<SERVER_A_IP>`
- `campus.remote.product-service.rpc-port` -> `9092`
- `campus.remote.favorite-service.host` -> `<SERVER_B_IP>`
- `campus.remote.favorite-service.rpc-port` -> `9093`
- `campus.remote.message-service.host` -> `<SERVER_B_IP>`
- `campus.remote.message-service.rpc-port` -> `9094`

Also keep protocol settings aligned (currently all are v1 + java serializer):

- `campus.remote.*.rpc-version` -> `1`
- `campus.remote.*.rpc-serializer` -> `java`

Notes:

- `base-url` fields are compatibility placeholders and not used by current gateway RPC path.
- HTTP ports are still required for `/actuator/health` and direct troubleshooting.

Target file:

- `gateway/src/main/resources/application.yml`

## 6. Notes

- Current scripts use `java -jar` with Spring Boot executable jars.
- If a service starts but health check is not UP, inspect its log file under `logs/server-a` or `logs/server-b`.
- `user-service`, `product-service`, `favorite-service`, and `message-service` are all DB-backed.
- Databases used: `campustrade_user`, `campustrade_product`, `campustrade_favorite`, `campustrade_message`.

## 7. systemd (optional)

If you want auto-start on boot and unified process management, use:

- `deploy/systemd/README.md`
- `deploy/systemd/install-server-a.sh`
- `deploy/systemd/install-server-b.sh`
