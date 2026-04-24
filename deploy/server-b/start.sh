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

echo "[info] start server B services..."
start_service "favorite-service" "8083"
start_service "message-service" "8084"

echo "[ok] server B services started"
