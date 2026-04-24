#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/server-b"
LOG_DIR="${APP_ROOT}/logs/server-b"

source "${APP_ROOT}/deploy/common/service-functions.sh"

echo "[info] server B status"
status_service "favorite-service" "8083"
status_service "message-service" "8084"

