#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/server-b"
LOG_DIR="${APP_ROOT}/logs/server-b"
ENV_FILE="${APP_ROOT}/deploy/env/server-b.env"

if [[ -f "${ENV_FILE}" ]]; then
  set -a
  source "${ENV_FILE}"
  set +a
fi

source "${APP_ROOT}/deploy/common/service-functions.sh"

ENABLE_FAVORITE_SERVICE_ON_B="${ENABLE_FAVORITE_SERVICE_ON_B:-true}"
ENABLE_MESSAGE_SERVICE_ON_B="${ENABLE_MESSAGE_SERVICE_ON_B:-true}"
FAVORITE_SERVICE_HTTP_PORT="${FAVORITE_SERVICE_HTTP_PORT:-8083}"
MESSAGE_SERVICE_HTTP_PORT="${MESSAGE_SERVICE_HTTP_PORT:-8084}"

echo "[info] server B status"
if [[ "${ENABLE_FAVORITE_SERVICE_ON_B,,}" == "true" ]]; then
  status_service "favorite-service" "${FAVORITE_SERVICE_HTTP_PORT}"
fi
if [[ "${ENABLE_MESSAGE_SERVICE_ON_B,,}" == "true" ]]; then
  status_service "message-service" "${MESSAGE_SERVICE_HTTP_PORT}"
fi
