#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/registry"
LOG_DIR="${APP_ROOT}/logs/registry"
ENV_FILE="${APP_ROOT}/deploy/env/registry.env"

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "[error] missing env file: ${ENV_FILE}"
  echo "[hint] cp deploy/env/registry.env.example deploy/env/registry.env"
  exit 1
fi

set -a
source "${ENV_FILE}"
set +a
source "${APP_ROOT}/deploy/common/service-functions.sh"

require_cmd java
require_java17

echo "[info] start registry-center..."
start_service "registry-center" "8070"
echo "[ok] registry-center started"

