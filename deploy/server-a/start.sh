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

echo "[info] start server A services..."
start_service "user-service" "8081"
start_service "product-service" "8082"

echo "[ok] server A services started"
