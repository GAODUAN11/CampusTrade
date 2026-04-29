#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/server-a"
LOG_DIR="${APP_ROOT}/logs/server-a"
ENV_FILE="${APP_ROOT}/deploy/env/server-a.env"

if [[ -f "${ENV_FILE}" ]]; then
  set -a
  source "${ENV_FILE}"
  set +a
fi

source "${APP_ROOT}/deploy/common/service-functions.sh"

ENABLE_USER_SERVICE_ON_A="${ENABLE_USER_SERVICE_ON_A:-true}"
ENABLE_PRODUCT_SERVICE_ON_A="${ENABLE_PRODUCT_SERVICE_ON_A:-true}"
ENABLE_FAVORITE_STANDBY_ON_A="${ENABLE_FAVORITE_STANDBY_ON_A:-false}"
USER_SERVICE_HTTP_PORT="${USER_SERVICE_HTTP_PORT:-8081}"
PRODUCT_SERVICE_HTTP_PORT="${PRODUCT_SERVICE_HTTP_PORT:-8082}"
FAVORITE_A_HTTP_PORT="${FAVORITE_A_HTTP_PORT:-8083}"

echo "[info] server A status"
if [[ "${ENABLE_USER_SERVICE_ON_A,,}" == "true" ]]; then
  status_service "user-service" "${USER_SERVICE_HTTP_PORT}"
fi
if [[ "${ENABLE_PRODUCT_SERVICE_ON_A,,}" == "true" ]]; then
  status_service "product-service" "${PRODUCT_SERVICE_HTTP_PORT}"
fi
if [[ "${ENABLE_FAVORITE_STANDBY_ON_A,,}" == "true" ]]; then
  status_service "favorite-service" "${FAVORITE_A_HTTP_PORT}"
fi
