#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMPLATE_DIR="${SCRIPT_DIR}/templates"
APP_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUN_USER="${SUDO_USER:-$(id -un)}"
START_NOW="false"

usage() {
  cat <<EOF
Usage:
  sudo bash deploy/systemd/install-server-b.sh [options]

Options:
  --app-root <path>      CampusTrade root path (default: script inferred root)
  --run-user <user>      Linux user to run services (default: current user)
  --start-now            Start server-b target immediately after install
  -h, --help             Show this help
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --app-root)
      APP_ROOT="$2"
      shift 2
      ;;
    --run-user)
      RUN_USER="$2"
      shift 2
      ;;
    --start-now)
      START_NOW="true"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "[error] unknown argument: $1"
      usage
      exit 1
      ;;
  esac
done

if [[ "${EUID}" -ne 0 ]]; then
  echo "[error] this script must run as root (use sudo)"
  exit 1
fi

if [[ ! -d "${APP_ROOT}" ]]; then
  echo "[error] app root does not exist: ${APP_ROOT}"
  exit 1
fi

if ! id -u "${RUN_USER}" >/dev/null 2>&1; then
  echo "[error] run user does not exist: ${RUN_USER}"
  exit 1
fi

for tpl in \
  campustrade-favorite.service.tpl \
  campustrade-message.service.tpl \
  campustrade-server-b.target.tpl; do
  if [[ ! -f "${TEMPLATE_DIR}/${tpl}" ]]; then
    echo "[error] missing template: ${TEMPLATE_DIR}/${tpl}"
    exit 1
  fi
done

render_unit() {
  local src="$1"
  local dst="$2"
  sed \
    -e "s|__APP_ROOT__|${APP_ROOT}|g" \
    -e "s|__RUN_USER__|${RUN_USER}|g" \
    "${src}" > "${dst}"
}

echo "[info] writing systemd units to /etc/systemd/system"
render_unit "${TEMPLATE_DIR}/campustrade-favorite.service.tpl" "/etc/systemd/system/campustrade-favorite.service"
render_unit "${TEMPLATE_DIR}/campustrade-message.service.tpl" "/etc/systemd/system/campustrade-message.service"
render_unit "${TEMPLATE_DIR}/campustrade-server-b.target.tpl" "/etc/systemd/system/campustrade-server-b.target"

systemctl daemon-reload
systemctl enable campustrade-server-b.target

if [[ ! -f "${APP_ROOT}/deploy/env/server-b.env" ]]; then
  echo "[warn] env file missing: ${APP_ROOT}/deploy/env/server-b.env"
  echo "[hint] cp ${APP_ROOT}/deploy/env/server-b.env.example ${APP_ROOT}/deploy/env/server-b.env"
fi

if [[ "${START_NOW}" == "true" ]]; then
  systemctl restart campustrade-server-b.target
  echo "[ok] server-b target started"
else
  echo "[ok] install complete"
  echo "[next] start with: systemctl start campustrade-server-b.target"
fi
