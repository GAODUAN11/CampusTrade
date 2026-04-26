#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_DIR="${APP_ROOT}/run/registry"
LOG_DIR="${APP_ROOT}/logs/registry"

source "${APP_ROOT}/deploy/common/service-functions.sh"

echo "[info] registry status"
status_service "registry-center" "8070"

