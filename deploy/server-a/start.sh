#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/server-a"
LOG_DIR="${APP_ROOT}/logs/server-a"
ENV_FILE="${APP_ROOT}/deploy/env/server-a.env"

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "[error] missing env file: ${ENV_FILE}"
  echo "[hint] cp deploy/env/server-a.env.example deploy/env/server-a.env"
  exit 1
fi

# Export all variables from env file for child java process.
set -a
source "${ENV_FILE}"
set +a
source "${APP_ROOT}/deploy/common/service-functions.sh"

require_cmd java
require_java17

ENABLE_USER_SERVICE_ON_A="${ENABLE_USER_SERVICE_ON_A:-true}"
ENABLE_PRODUCT_SERVICE_ON_A="${ENABLE_PRODUCT_SERVICE_ON_A:-true}"
ENABLE_FAVORITE_STANDBY_ON_A="${ENABLE_FAVORITE_STANDBY_ON_A:-false}"

USER_SERVICE_HTTP_PORT="${USER_SERVICE_HTTP_PORT:-8081}"
USER_SERVICE_RPC_PORT="${USER_SERVICE_RPC_PORT:-9091}"
PRODUCT_SERVICE_HTTP_PORT="${PRODUCT_SERVICE_HTTP_PORT:-8082}"
PRODUCT_SERVICE_RPC_PORT="${PRODUCT_SERVICE_RPC_PORT:-9092}"
FAVORITE_A_HTTP_PORT="${FAVORITE_A_HTTP_PORT:-8083}"
FAVORITE_A_RPC_PORT="${FAVORITE_A_RPC_PORT:-9093}"

echo "[info] start server A services..."
if [[ "${ENABLE_USER_SERVICE_ON_A,,}" == "true" ]]; then
  start_service \
    "user-service" \
    "${USER_SERVICE_HTTP_PORT}" \
    "SERVER_PORT=${USER_SERVICE_HTTP_PORT} CAMPUS_RPC_PORT=${USER_SERVICE_RPC_PORT}"
fi
if [[ "${ENABLE_PRODUCT_SERVICE_ON_A,,}" == "true" ]]; then
  start_service \
    "product-service" \
    "${PRODUCT_SERVICE_HTTP_PORT}" \
    "SERVER_PORT=${PRODUCT_SERVICE_HTTP_PORT} CAMPUS_RPC_PORT=${PRODUCT_SERVICE_RPC_PORT}"
fi
if [[ "${ENABLE_FAVORITE_STANDBY_ON_A,,}" == "true" ]]; then
  start_service \
    "favorite-service" \
    "${FAVORITE_A_HTTP_PORT}" \
    "SERVER_PORT=${FAVORITE_A_HTTP_PORT} CAMPUS_RPC_PORT=${FAVORITE_A_RPC_PORT}"
fi

echo "[ok] server A services started"
