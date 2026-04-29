#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/server-b"
LOG_DIR="${APP_ROOT}/logs/server-b"
ENV_FILE="${APP_ROOT}/deploy/env/server-b.env"

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "[error] missing env file: ${ENV_FILE}"
  echo "[hint] cp deploy/env/server-b.env.example deploy/env/server-b.env"
  exit 1
fi

# Export all variables from env file for child java process.
set -a
source "${ENV_FILE}"
set +a
source "${APP_ROOT}/deploy/common/service-functions.sh"

require_cmd java
require_java17

ENABLE_FAVORITE_SERVICE_ON_B="${ENABLE_FAVORITE_SERVICE_ON_B:-true}"
ENABLE_MESSAGE_SERVICE_ON_B="${ENABLE_MESSAGE_SERVICE_ON_B:-true}"

FAVORITE_SERVICE_HTTP_PORT="${FAVORITE_SERVICE_HTTP_PORT:-8083}"
FAVORITE_SERVICE_RPC_PORT="${FAVORITE_SERVICE_RPC_PORT:-9093}"
MESSAGE_SERVICE_HTTP_PORT="${MESSAGE_SERVICE_HTTP_PORT:-8084}"
MESSAGE_SERVICE_RPC_PORT="${MESSAGE_SERVICE_RPC_PORT:-9094}"

echo "[info] start server B services..."
if [[ "${ENABLE_FAVORITE_SERVICE_ON_B,,}" == "true" ]]; then
  start_service \
    "favorite-service" \
    "${FAVORITE_SERVICE_HTTP_PORT}" \
    "SERVER_PORT=${FAVORITE_SERVICE_HTTP_PORT} CAMPUS_RPC_PORT=${FAVORITE_SERVICE_RPC_PORT}"
else
  echo "[skip] favorite-service disabled by config"
fi
if [[ "${ENABLE_MESSAGE_SERVICE_ON_B,,}" == "true" ]]; then
  start_service \
    "message-service" \
    "${MESSAGE_SERVICE_HTTP_PORT}" \
    "SERVER_PORT=${MESSAGE_SERVICE_HTTP_PORT} CAMPUS_RPC_PORT=${MESSAGE_SERVICE_RPC_PORT}"
else
  echo "[skip] message-service disabled by config"
fi

echo "[ok] server B services started"
