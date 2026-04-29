#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

echo "[info] APP_ROOT=${APP_ROOT}"
cd "${APP_ROOT}"

echo "[info] building runnable jars for server A modules..."
mvn -DskipTests -pl user-service,product-service,favorite-service -am clean package

echo "[ok] prepare done for server A"
