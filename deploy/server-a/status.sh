#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/server-a"
LOG_DIR="${APP_ROOT}/logs/server-a"

source "${APP_ROOT}/deploy/common/service-functions.sh"

echo "[info] server A status"
status_service "user-service" "8081"
status_service "product-service" "8082"

